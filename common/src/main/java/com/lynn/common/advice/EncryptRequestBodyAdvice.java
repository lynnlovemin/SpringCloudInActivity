package com.lynn.common.advice;

import com.lynn.common.annotation.DisabledEncrypt;
import com.lynn.common.properties.EncryptProperties;
import com.lynn.common.utils.AesEncryptUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@ControllerAdvice
public class EncryptRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Autowired
    private EncryptProperties encryptProperties;

    @Override
    public boolean supports(MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return !parameter.getMethod().isAnnotationPresent(DisabledEncrypt.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        try {
                return new DecryptHttpInputMessage(httpInputMessage, encryptProperties.getKey(), encryptProperties.getCharset());
            } catch (Exception e) {
                throw new RuntimeException("解密失败！");
            }
    }
}

class DecryptHttpInputMessage implements HttpInputMessage {
    private HttpHeaders headers;
    private InputStream body;

    public DecryptHttpInputMessage(HttpInputMessage inputMessage, String key, String charset) throws Exception {
        this.headers = inputMessage.getHeaders();
        String content = IOUtils.toString(inputMessage.getBody(), charset);
        String descryptContent = AesEncryptUtils.aesDecrypt(content,key);
        this.body = IOUtils.toInputStream(descryptContent, charset);
    }

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
