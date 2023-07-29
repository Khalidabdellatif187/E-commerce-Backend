package com.example.Ecommerce.Backend;

import com.example.Ecommerce.Backend.Dto.CategoryDto;
import com.example.Ecommerce.Backend.Entity.CategoryEntity;
import com.example.Ecommerce.Backend.Mapper.CategoryMapper;
import com.example.Ecommerce.Backend.ServiceImpl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ECommerceBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CategoryServiceImpl service;

	@Autowired
	private CategoryMapper categoryMapper;

	private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	@Container
	public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("testDB")
			.withUsername("mysql")
			.withPassword("123");

	@DynamicPropertySource
	public static void setDataSourceProperty(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url" ,mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username" , mySQLContainer::getUsername);
		registry.add("spring.datasource.password" , mySQLContainer::getPassword);
	}

	@BeforeEach
	public void setup(){
		MockitoAnnotations.openMocks(this);
		mySQLContainer.start();
	}

	@Test
	@DisplayName("Test Create New Category")
	public void testCreateCategory() throws Exception{

		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setId(1L);
		categoryEntity.setCategoryName("clothes");
		categoryEntity.setDescription("Five Stars World Class Clothes With Best TradeMarks In The World");
		categoryEntity.setImageUrl("http://example.com/image.jpg");
		CategoryDto categoryDto = categoryMapper.toCategoryDto(categoryEntity);

		mockMvc.perform(post("/category/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryDto)))
				.andDo(print())
				.andExpect(status().isCreated());

	}

}
