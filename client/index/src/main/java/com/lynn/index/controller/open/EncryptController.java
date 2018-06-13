package com.lynn.index.controller.open;

import com.lynn.common.annotation.DisabledEncrypt;
import com.lynn.common.result.SingleResult;
import com.lynn.index.request.KeyRequest;
import com.lynn.index.response.KeyResponse;
import com.lynn.index.response.RSAResponse;
import com.lynn.index.service.EncryptOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 客户端启动，发送请求到服务端，服务端用RSA算法生成一对公钥和私钥，我们简称为pubkey1,prikey1，将公钥pubkey1返回给客户端。
 客户端拿到服务端返回的公钥pubkey1后，自己用RSA算法生成一对公钥和私钥，我们简称为pubkey2,prikey2，
 并将公钥pubkey2通过公钥pubkey1加密，加密之后传输给服务端。
 此时服务端收到客户端传输的密文，用私钥prikey1进行解密，因为数据是用公钥pubkey1加密的，
 通过解密就可以得到客户端生成的公钥pubkey2
 然后自己在生成对称加密，也就是我们的AES,其实也就是相对于我们配置中的那个16的长度的加密key,
 生成了这个key之后我们就用公钥pubkey2进行加密，返回给客户端，因为只有客户端有pubkey2对应的私钥prikey2，
 只有客户端才能解密，客户端得到数据之后，用prikey2进行解密操作，
 得到AES的加密key,最后就用加密key进行数据传输的加密，至此整个流程结束。
 */
@RestController
@RequestMapping("/open/encrypt")
public class EncryptController {

    @Autowired
    private EncryptOpenService encryptOpenService;

    @RequestMapping(value = "getRSA",method = RequestMethod.POST)
    @DisabledEncrypt
    public SingleResult<RSAResponse> getRSA(){
        return encryptOpenService.getRSA();
    }

    @RequestMapping(value = "getKey",method = RequestMethod.POST)
    @DisabledEncrypt
    public SingleResult<KeyResponse> getKey(@Valid @RequestBody KeyRequest request)throws Exception{
        return encryptOpenService.getKey(request);
    }


}
