package com.example.ecom_proj.controller;

import com.example.ecom_proj.model.Product;
import com.example.ecom_proj.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){
        Product product = service.getProductsById(id);
        if(product!=null)
            return new ResponseEntity<>(product, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile){

        try {
            Product product1 = service.addProduct(product,imageFile);
            return new ResponseEntity<>(product1,HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId){

        Product product = service.getProductsById(productId);
        byte[] imageFile = product.getImageData();

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(product.getImageType()))
                .body(imageFile);
    }
    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id,@RequestPart Product product, @RequestPart MultipartFile imageFile){
        Product product1 = null;
        try {
            product1 = service.updateProduct(id,product,imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(product1 != null){
            return new ResponseEntity<>("Updated",HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
    }
    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        Product product = service.getProductsById(id);
        if(product!=null) {
            service.deleteProduct(id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("Product not found",HttpStatus.NOT_FOUND);
    }
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        System.out.println("Searching "+keyword);
        List<Product> products = service.searchProducts(keyword);
        System.out.println("Searching "+ products.get(0).getVehicleName());
        return new ResponseEntity<>(products,HttpStatus.OK);
    }

    //
    //
    //
    //working on javaScript page
    @PostMapping("products/add")
    public ResponseEntity<String> addProduct(@RequestParam("product") String productData,
                                             @RequestParam("image") MultipartFile image) throws IOException {
//        System.out.println(productData);
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productData, Product.class);
        service.addProduct(product,image);
        return ResponseEntity.ok("Product added successfully");
    }
    @PutMapping("products/add/{id}")
    public ResponseEntity<String> update(@PathVariable("id") int id,@RequestParam("product") String productData,
                                             @RequestPart("image") MultipartFile image) throws IOException {
        System.out.println(productData);
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productData, Product.class);
        Product product1 = service.getProductsById(id);

        if(product1 != null){
            product1.setVehicle(product.getVehicle());
            product1.setVehicleName(product.getVehicleName());
            product1.setBrand(product.getBrand());
            product1.setDescription(product.getDescription());
            product1.setPrice(product.getPrice());
            product1.setReleaseDate(product.getReleaseDate());
            product1.setAvailable(product.isAvailable());
            product1.setQuantity(product.getQuantity());
            product1.setLocation(product.getLocation());
            product1.setOwner(product.getOwner());

            if (image != null && !image.isEmpty()) {
                product1.setImageData(image.getBytes());
                product1.setImageName(image.getOriginalFilename());
                product1.setImageType(image.getContentType());
            }

            service.update(product1);
            return ResponseEntity.ok("Product updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //
    //
    //

}
