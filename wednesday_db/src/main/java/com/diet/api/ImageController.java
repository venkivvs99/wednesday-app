package com.diet.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins="*")
public class ImageController {
	
	@Autowired
	ProductRepo prdRepo;
	
	@PostMapping("/prd/add")
	public String addProduct(
			@RequestParam("imageFile") MultipartFile productFile,
			String name,
			String price) {
		
		try {
			System.out.println("Original Image Byte Size - " + productFile.getBytes().length);
			
			Product img = new Product(name,price,
					compressBytes(productFile.getBytes()));
			prdRepo.save(img);
						
		}catch(Exception e) {
			
		}
		return "Added New Products";
	}
	
	@GetMapping("/prd/all")
	public List<Product> getAllProducts(){
		List<Product> resList = new ArrayList<Product>();
		
		List<Product> prdList = prdRepo.findAll();
		Product product=null;
		for(int i=0;i<prdList.size();i++) {
			product = prdList.get(i);
			product = new Product("Apple","Rs.120/-",
					decompressBytes(product.getImage()));
			resList.add(product);
		}
			
		return resList;
	}
	
	// compress the image bytes before storing it in the database
		public static byte[] compressBytes(byte[] data) {
			Deflater deflater = new Deflater();
			deflater.setInput(data);
			deflater.finish();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			try {
				outputStream.close();
			} catch (IOException e) {
			}
			System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

			return outputStream.toByteArray();
		}


		// uncompress the image bytes before returning it to the angular application
		public static byte[] decompressBytes(byte[] data) {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			try {
				while (!inflater.finished()) {
					int count = inflater.inflate(buffer);
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
			} catch (IOException ioe) {
			} catch (DataFormatException e) {
			}
			return outputStream.toByteArray();
		}
		

}