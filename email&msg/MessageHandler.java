package com.wailian.util;

import com.wailian.entity.MessageConfig;
import com.wailian.repository.MessageConfigRepository;
import com.wailian.repository.SMTPConfigRepository;
import com.wailian.service.CustomerService;

import lombok.extern.log4j.Log4j;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jet.chen on 5/17/2017.
 */
@Service
@Log4j
public class MessageHandler {

	@Autowired
    MessageConfigRepository messageConfigRepository;
	
	public String sendMessage(String mobileNumber, String content, MessageConfig messageConfig) throws Exception {
		String result = "1";
		Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("mobile", mobileNumber);
        requestBody.put("content", content);
        requestBody.put("campaignID", 1);
        
        if(messageConfig == null){
        	Iterable<MessageConfig> messageConfigs = messageConfigRepository.findAll();
            if (messageConfigs.iterator().hasNext()) {
                messageConfig = messageConfigs.iterator().next();
            } else {
                throw new Exception("没有短信接口配置，请配置后再试！");
            }
		}
		
        String password = messageConfig.getPassword();
        String username = messageConfig.getUserName();
        String interfaceAddress = messageConfig.getInterfaceAddress();
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(username) || StringUtils.isEmpty(interfaceAddress)){
            throw new Exception("接口地址，用户名，密码不能为空！");
        }
        try{
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        BASE64Encoder encoder = new BASE64Encoder();
	        String encoding = encoder.encode((username + ":" + password).getBytes());
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.add("Authorization", "Basic " + encoding);
	        HttpEntity entity = new HttpEntity(requestBody, headers);
	        ResponseEntity<HashMap> response = restTemplate.exchange(interfaceAddress, HttpMethod.POST, entity, HashMap.class);
	        if (response.getStatusCodeValue() != HttpURLConnection.HTTP_OK) {
				return "0";
			}
        }
        catch (Exception e) {
        	e.printStackTrace();
			log.error("Send message error", e);
			return "0";
		}
        return result;
        
    }
	
	public String send(String mobileNumber, String content, MessageConfig messageConfig) {
		if(messageConfig == null){
			messageConfig = messageConfigRepository.findOne((long)1);
		}
		String result = "1";
		try{
			String postUrl = messageConfig.getInterfaceAddress(); //properties.getProperty("postUrl");
			String sname = messageConfig.getUserName(); //properties.getProperty("sname");
			String spwd = messageConfig.getPassword(); //properties.getProperty("spwd");
			String scorpid = messageConfig.getScorpid(); //properties.getProperty("scorpid");
			String sprdid = messageConfig.getSprdid(); //properties.getProperty("sprdid");

			StringBuilder postData = new StringBuilder();
			postData.append("sname=").append(sname);
			postData.append("&spwd=").append(spwd);
			postData.append("&&scorpid=").append(scorpid);
			postData.append("&&sprdid=").append(sprdid);
			postData.append("&sdst=").append(mobileNumber);
			String SMSContent = java.net.URLEncoder.encode(content, "utf-8"); // content + signature
			postData.append("&smsg=").append(SMSContent);

			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", "" + postData.length());
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(postData.toString());
			out.flush();
			out.close();
			log.info("Send message response code: " + conn.getResponseCode() + ". Message: " + conn.getResponseMessage());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return "0";
			}

			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			while ((line = reader.readLine()) != null) {
				result += line + "\n";
			}

			reader.close();
			return result;
		} catch(IOException e){
			e.printStackTrace();
			log.error("Send message error", e);
			return "0";
		}
	}
}
