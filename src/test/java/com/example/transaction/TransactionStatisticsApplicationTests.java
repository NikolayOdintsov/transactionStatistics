package com.example.transaction;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionStatisticsApplicationTests {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation).uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    public void getStatistics() throws Exception {

        RestDocumentationResultHandler document = this.documentPrettyPrintReqResp("statistics");

        this.mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(this.contentType))
                .andExpect(content().string(""))
                .andDo(document);
    }

    @Test
    public void createTransactionsSuccess() throws Exception {

        RestDocumentationResultHandler snippet = this.documentPrettyPrintReqResp("transactions-success");

        long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
        JSONObject requestBody = new JSONObject();
        requestBody.put("amount", 12.3);
        requestBody.put("timestamp", timestamp);

        this.mockMvc.perform(post("/transactions")
                .contentType(this.contentType)
                .content(requestBody.toString()))
                .andExpect(status().isCreated())
                .andExpect(content().string(""))
                .andDo(snippet.document(
                        PayloadDocumentation.requestFields(new FieldDescriptor[]{
                                PayloadDocumentation.fieldWithPath("amount").description("transaction amount"),
                                PayloadDocumentation.fieldWithPath("timestamp").description("transaction time in epoch in millis in UTC time zone (this is not current timestamp)")
                        })
                ));
    }

    @Test
    public void createTransactionsNoContent() throws Exception {

        RestDocumentationResultHandler snippet = this.documentPrettyPrintReqResp("transactions-no-content");

        JSONObject requestBody = new JSONObject();
        requestBody.put("amount", 12.3);
        requestBody.put("timestamp", 1478192204000l);

        this.mockMvc.perform(post("/transactions")
                .contentType(this.contentType)
                .content(requestBody.toString()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(snippet);
    }

    /**
     * Pretty print request and response
     *
     * @param useCase the name of the snippet
     * @return RestDocumentationResultHandler
     */
    private RestDocumentationResultHandler documentPrettyPrintReqResp(String useCase) {
        return document(useCase,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));
    }
}
