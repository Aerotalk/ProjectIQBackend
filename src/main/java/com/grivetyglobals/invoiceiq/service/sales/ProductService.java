package com.grivetyglobals.invoiceiq.service.sales;

import com.grivetyglobals.invoiceiq.dto.sales.ProductDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.sales.Product;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.sales.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    public List<ProductDto> getProductsByCompany(UUID companyId) {
        return productRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProduct(UUID id) {
        return mapToDto(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Transactional
    public ProductDto createProduct(UUID companyId, ProductDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Product product = new Product();
        product.setCompany(company);
        mapToEntity(dto, product);

        return mapToDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(UUID id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        mapToEntity(dto, product);

        return mapToDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    private void mapToEntity(ProductDto dto, Product product) {
        product.setItemCode(dto.getItemCode());
        product.setItemName(dto.getItemName());
        product.setDescription(dto.getDescription());
        product.setType(dto.getType());
        product.setUnit(dto.getUnit());
        product.setStandardRate(dto.getStandardRate());
        product.setHsnSac(dto.getHsnSac());
        product.setGstRate(dto.getGstRate());
        product.setStatus(dto.getStatus());
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setItemCode(product.getItemCode());
        dto.setItemName(product.getItemName());
        dto.setDescription(product.getDescription());
        dto.setType(product.getType());
        dto.setUnit(product.getUnit());
        dto.setStandardRate(product.getStandardRate());
        dto.setHsnSac(product.getHsnSac());
        dto.setGstRate(product.getGstRate());
        dto.setStatus(product.getStatus());
        return dto;
    }
}
