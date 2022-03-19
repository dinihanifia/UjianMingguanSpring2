package com.juaracoding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juaracoding.config.JwtTokenUtil;
import com.juaracoding.model.BookingModel;
import com.juaracoding.model.BusModel;
import com.juaracoding.model.KeberangkatanCustomModel;
import com.juaracoding.model.PenumpangModel;
import com.juaracoding.repository.BookingRepository;
import com.juaracoding.repository.BusRepository;
import com.juaracoding.repository.KeberangkatanRepository;
import com.juaracoding.repository.PenumpangRepository;
import com.juaracoding.service.JwtPenumpangDetailService;

@RestController
@RequestMapping("/busbookingsystem")
public class BusController {
	
	@Autowired
	BusRepository busRepo;
	
	@Autowired
	KeberangkatanRepository keberangkatanRepo;
	
	@Autowired
	BookingRepository bookingRepo;
	
	@Autowired
	PenumpangRepository penumpangRepo;
	
	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
	JwtPenumpangDetailService jwtPenumpangDetailService;
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	PasswordEncoder passEncod;
	
	@GetMapping("/")
	private List<BusModel>getAll(){
		return busRepo.findAll();
	}
	
	@GetMapping("/findKeberangkatan")
	private List<KeberangkatanCustomModel> getDataByTerminalAwal(@RequestParam(name="terminalAwal")String terminalAwal,
			@RequestParam(name="tanggal")String tanggal) {
		return keberangkatanRepo.getAllDataTerminalAwalOrTanggal(terminalAwal, tanggal);
	}
	
	
//	http://localhost:8080/busbookingsystem/booking
	@PostMapping("/booking")
	private String saveDataSeat(@RequestBody PenumpangModel penumpang) {
		penumpangRepo.save(penumpang);
		return "Pemesanan bus berhasil ";
	}
	
	@PostMapping("/cancel")
	private String deleteByBookingId(@RequestParam(name="id") long id) {
		bookingRepo.deleteById(id);
		return "Pemesanan dengan nomor id " +id +" telah dibatalkan";
	}
	
	@PostMapping("/register")
	private ResponseEntity<String> savePenumpang(@RequestBody PenumpangModel penumpang){
		penumpang.setPassword(passEncod.encode(penumpang.getPassword()));
		penumpangRepo.save(penumpang);
		return ResponseEntity.status(HttpStatus.CREATED).body("Akun berhasil dibuat");
	}
	
	@PostMapping("/login")
	private ResponseEntity<?> login(@RequestBody PenumpangModel PenumpangModel) throws Exception {
		authenticate(PenumpangModel.getUsername(),PenumpangModel.getPassword());
		final UserDetails userDetails = jwtPenumpangDetailService.loadUserByUsername(PenumpangModel.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(token);
	}
	
	private void authenticate(String username, String password) throws Exception {
		try {
			authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch(DisabledException e) {
//			user disabled
			throw new Exception("USER_DISABLED", e);
		} catch(BadCredentialsException e) {
//			invalid credentials
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
	
	

	
	
	


}
