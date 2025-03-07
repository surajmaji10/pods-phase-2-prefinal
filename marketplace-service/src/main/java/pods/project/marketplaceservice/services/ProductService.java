package pods.project.marketplaceservice.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pods.project.marketplaceservice.entities.Product;
import pods.project.marketplaceservice.repositories.ProductsRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Service
public class ProductService {
    private final ProductsRepository productsRepository;
    private RestTemplate restTemplate;

    @Value("${host.url}")
    private String localhost;
    @Value("${account.service.url}")
    private String accountServiceUrl;
    @Value("${wallet.service.url}")
    private String walletServiceUrl;
    @Value("${marketplace.service.url}")
    private String marketplaceServiceUrl;

    @Autowired
    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = new ArrayList<>();
        products = productsRepository.findAll();
        return  ResponseEntity.ok().body(products);
    }

    @Transactional
    public ResponseEntity<?> getProductById(Integer id) {
        List<Product> products = productsRepository.findProductByIdIs(id);
        if(products.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(productNotFound(id));
        }
        return  ResponseEntity.ok().body(products.get(0));
    }

    @Transactional
    public ResponseEntity<?> updateProducts(Map<String, Object> request) {
        String order_id = request.get("order_id").toString();
        List<Map<String, Integer>> products = (List<Map<String, Integer>>) request.get("products");

        for(Map<String, Integer> product: products){
            Integer id = product.get("product_id");
            Integer quantity = product.get("quantity");
            Integer version = product.get("version");
            productsRepository.updateQuantity(id, quantity);
        }

        return ResponseEntity.ok().body(productsUpdated(order_id));
    }

    private String productsUpdated(String orderId) {
        return "Products updated for order with id=" + orderId;
    }

    private static String productNotFound(Integer id) {
        return  "Product with id=" + id + " not found";
    }

    public String processExcelFile() {
        try (FileInputStream fileInputStream = new FileInputStream("products.xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;

                Cell idCell = row.getCell(0);
                Cell nameCell = row.getCell(1);
                Cell descriptionCell = row.getCell(2);
                Cell priceCell = row.getCell(3);
                Cell stockQuantityCell = row.getCell(4);

                Integer id = (int) idCell.getNumericCellValue();
                Product product = productsRepository.findById(id).orElse(new Product());

                product.setId(id);
                product.setName(nameCell.getStringCellValue());
                product.setDescription(descriptionCell.getStringCellValue());
                product.setPrice((int) priceCell.getNumericCellValue());
                product.setStock_quantity((int) stockQuantityCell.getNumericCellValue());

                productsRepository.save(product);
            }

            workbook.close();
            return "Products Populated Successfully";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to populate products: " + e.getMessage();
        }
    }


}
