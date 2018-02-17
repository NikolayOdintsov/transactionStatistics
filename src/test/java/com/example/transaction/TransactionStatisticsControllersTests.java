package com.example.transaction;

import com.example.transaction.controllers.StatisticsController;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionStatistics;
import com.example.transaction.service.TransactionService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionStatisticsControllersTests {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransactionService transactionServiceMock;

    @Autowired
    private StatisticsController statisticsController;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation).uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();

        StatisticsController unwrappedController = (StatisticsController) unwrapProxy(statisticsController);
        ReflectionTestUtils.setField(unwrappedController, "transactionService", transactionServiceMock);
    }

    @Test
    public void getStatistics() throws Exception {
        //given
        RestDocumentationResultHandler document = this.documentPrettyPrintReqResp("statistics");

        TransactionStatistics statistics = getMockedTransactionStatistics();
        Mockito.when(this.transactionServiceMock.getTransactionStatistics()).thenReturn(statistics);

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        //then
        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(this.contentType))
                .andExpect(content().string(statistics.toString()))
                .andDo(document);
    }

    @Test
    public void createTransactionsSuccess() throws Exception {
        //given
        RestDocumentationResultHandler snippet = this.documentPrettyPrintReqResp("transactions-success");

        long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
        JSONObject requestBody = new JSONObject();
        requestBody.put("amount", 12.3);
        requestBody.put("timestamp", timestamp);

        //when
        MvcResult mvcResult = this.mockMvc.perform(post("/transactions")
                .contentType(this.contentType)
                .content(requestBody.toString()))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        //then
        this.mockMvc.perform(asyncDispatch(mvcResult))
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
        //given
        RestDocumentationResultHandler snippet = this.documentPrettyPrintReqResp("transactions-no-content");

        JSONObject requestBody = new JSONObject();
        requestBody.put("amount", 12.3);
        requestBody.put("timestamp", 1478192204000l);

        //when
        MvcResult mvcResult = this.mockMvc.perform(post("/transactions")
                .contentType(this.contentType)
                .content(requestBody.toString()))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        //then
        this.mockMvc.perform(asyncDispatch(mvcResult))
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

    private static final Object unwrapProxy(Object bean) throws Exception {
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            bean = advised.getTargetSource().getTarget();
        }
        return bean;
    }

    private TransactionStatistics getMockedTransactionStatistics() {
        Map<UUID, Transaction> transactions = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
            Transaction tz = new Transaction(timestamp, i);
            transactions.put(UUID.randomUUID(), tz);
        }

        /**
         grabbed code snippet from {@link com.example.transaction.service.TransactionServiceImpl#getTransactionStatistics}
         */
        return transactions.values().stream()
                .filter(Transaction::isValid)
                .mapToDouble(Transaction::getAmount)
                .collect(TransactionStatistics::new,
                        TransactionStatistics::accept,
                        TransactionStatistics::combine);
    }
}
