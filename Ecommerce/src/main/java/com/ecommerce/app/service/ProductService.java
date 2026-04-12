package com.ecommerce.app.service;

import com.ecommerce.app.dto.ProductRequestDto;
import com.ecommerce.app.dto.ProductResponseDto;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
@RequiredArgsConstructor  // Lombok generates constructor for all final fields = constructor injection
public class ProductService {

    private final ProductRepository productRepository;

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    public ProductResponseDto createProduct(ProductRequestDto request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    // -------------------------------------------------------
    // READ ALL
    // -------------------------------------------------------
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -------------------------------------------------------
    // READ ONE
    // -------------------------------------------------------
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product Not Found "+id, id));

        return mapToResponse(product);
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found "+id, id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found "+id, id));

        productRepository.delete(product);
    }

    // -------------------------------------------------------
    // HELPER — Entity → DTO conversion
    // -------------------------------------------------------
    private ProductResponseDto mapToResponse(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
    
    
    
    public Page<ProductResponseDto> searchProducts(
            String name, String category, int page, int size, String sortBy) {

        // Build Pageable — page number, size, sort field
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

        Page<Product> products;

        if (name != null && category != null) {
            // Both filters
            products = productRepository
                    .findByNameContainingIgnoreCaseAndCategory(name, category, pageable);

        } else if (name != null) {
            // Search by name only
            products = productRepository
                    .findByNameContainingIgnoreCase(name, pageable);

        } else if (category != null) {
            // Filter by category only
            products = productRepository
                    .findByCategory(category, pageable);

        } else {
            // No filter — just paginate all
            products = productRepository.findAll(pageable);
        }

        return products.map(this::mapToResponse);
    }
}