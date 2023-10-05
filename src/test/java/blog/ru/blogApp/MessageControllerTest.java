package blog.ru.blogApp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails("user2")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/messages-list-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MessageControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/message"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='navbarSupportedContent']/span/p")
                        .string("user2"));
    }


    @Test
    public void messageListTest() throws Exception {
        this.mockMvc.perform(get("/message"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(4));
    }

    @Test
    public void filterMessageTest() throws Exception {

        this.mockMvc.perform(
                multipart("/message/search")
                        .param("query","my").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list2']/div").nodeCount(2))
                .andExpect(xpath("//div[@id='message-list2']/div/div[@data-id=1]").exists())
                .andExpect(xpath("//div[@id='message-list2']/div/div[@data-id=3]").exists());
    }

    @Test
    public void addMessageToListTest() throws Exception{
        MockHttpServletRequestBuilder multipart = multipart("/message")
                .file("file", "123".getBytes())
                .param("text", "newText")
                .param("tag", "new")
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/message"));

        this.mockMvc.perform(get("/message"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(5))
                .andExpect((xpath("//div[@id='message-list']/div[1]/div/div[2]/h5").string("new")))
                .andExpect((xpath("//div[@id='message-list']/div[1]/div/div[2]/p").string("newText")));
    }

}



