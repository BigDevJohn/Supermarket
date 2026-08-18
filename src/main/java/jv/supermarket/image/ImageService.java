package jv.supermarket.image;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jv.supermarket.product.Product;
import jv.supermarket.product.ProductService;
import jv.supermarket.shared.customexception.ImageSavingException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    private final ProductService productService;

    ImageService(ImageRepository imageRepository, ProductService productService) {
        this.imageRepository = imageRepository;
        this.productService = productService;
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + id));
    }

    public List<ImageDTO> getImagesByProductId(Long productId) {
        List<Image> images = imageRepository.findByProductId(productId);
        if (images == null) {
            throw new ResourceNotFoundException("No images found for product with id: " + productId);
        }
        return images.stream().map(image -> convertToDTO(image)).collect(Collectors.toList());
    }

    public List<ImageDTO> saveImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);

        List<ImageDTO> savedImages = new ArrayList<ImageDTO>();
        for (MultipartFile file : files) {
            try {
                Image image = new Image();

                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                Image savedImage = imageRepository.save(image);

                savedImage.setDownloadUrl("/supermarket/image/" + savedImage.getId() + "/download");
                imageRepository.save(savedImage);

                savedImages.add(convertToDTO(savedImage));

            } catch (IOException | SQLException e) {
                throw new ImageSavingException("Error saving the provided images");
            }
        }
        return savedImages;
    }

    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new ImageSavingException("Error updating the requested images");
        }
    }

    public void deleteImage(Long imageId) {
        if (imageRepository.existsById(imageId)) {
            imageRepository.deleteById(imageId);
        } else {
            throw new ResourceNotFoundException("Image with id: " + imageId + " not found");
        }
    }

    public ImageDTO convertToDTO(Image image) {
        ImageDTO dto = new ImageDTO();
        BeanUtils.copyProperties(image, dto);
        return dto;
    }
}
