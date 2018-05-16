package com.asiainfo.iop.channel.interact.api;

import com.asiainfo.iop.channel.interact.IOPRecApp;
import com.asiainfo.iop.channel.interact.service.RecService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IOPRecApp.class)
@WebAppConfiguration
public class RecControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RecService recService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void recTest001() throws Exception {
        long ts = System.currentTimeMillis();
        String chlId = "chl001";
        String uid = "13511011100";
        String sign = recService.getSign(chlId, uid, ts);

        mockMvc.perform(
                get("/rec")
                        .param("chl_id", chlId)
                        .param("uid", uid)
                        .param("ts", "" + ts)
                        .param("sign", sign)
        )
                .andExpect(content().contentType(contentType))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is(0)))
                .andExpect(jsonPath("$.msg", is("ok")))
                .andDo(result->result.getResponse().getContentAsString());
    }
    @Test
    public void recTest002() throws Exception {
        long ts = System.currentTimeMillis() + RecController.TIME_RANGE + 10;
        String chlId = "chl001";
        String uid = "13511011100";
        String sign = recService.getSign(chlId, uid, ts);

        mockMvc.perform(
                get("/rec")
                        .param("chl_id", chlId)
                        .param("uid", uid)
                        .param("ts", "" + ts)
                        .param("sign", sign)
        )
                .andExpect(content().contentType(contentType))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status", is(-1)))
                .andExpect(jsonPath("$.msg", is("请核对请求参数")))
                .andDo(result->result.getResponse().getContentAsString());
    }
    @Test
    public void recTest003() throws Exception {
        long ts = System.currentTimeMillis();
        String chlId = "chl001";
        String uid = "13511011100";
        String sign = recService.getSign(chlId, uid, ts)+"xx";

        mockMvc.perform(
                get("/rec")
                        .param("chl_id", chlId)
                        .param("uid", uid)
                        .param("ts", "" + ts)
                        .param("sign", sign)
        )
                .andExpect(content().contentType(contentType))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.status", is(-2)))
                .andExpect(jsonPath("$.msg", is("请核对请求参数")))
                .andDo(result->result.getResponse().getContentAsString());
    }
}