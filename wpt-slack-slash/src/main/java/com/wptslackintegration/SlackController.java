package com.wptslackintegration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.view.View;
import com.slack.api.util.json.GsonFactory;

@RestController
@Component
public class SlackController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SlackService slackService;

	@Value(value = "${ngrokurl}")
	private String ngrokUrl;

	@Value(value = "${bottoken}")
	private String botToken;

	Slack slack = Slack.getInstance();

	@PostMapping(value = "/submittest")
	public void getPerfDetail(HttpServletRequest req, HttpServletResponse res)
			throws InterruptedException, IOException, SlackApiException {

		res.setStatus(HttpServletResponse.SC_OK);

		String json = "{\n\t\"title\": {\n\t\t\"type\": \"plain_text\",\n\t\t\"text\": \"My app\"\n\t},\n\t\"submit\": {\n\t\t\"type\": \"plain_text\",\n\t\t\"text\": \"Submit\"\n\t},\n\t\"blocks\": [\n\t\t{\n\t\t\t\"type\": \"input\",\n\t\t\t\"block_id\": \"input-url\",\n\t\t\t\"element\": {\n\t\t\t\t\"type\": \"plain_text_input\",\n\t\t\t\t\"action_id\": \"plain_text_input-action\"\n\t\t\t},\n\t\t\t\"label\": {\n\t\t\t\t\"type\": \"plain_text\",\n\t\t\t\t\"text\": \"Enter URL\",\n\t\t\t\t\"emoji\": true\n\t\t\t}\n\t\t},\n\t\t{\n\t\t\t\"type\": \"input\",\n\t\t\t\"block_id\": \"input-key\",\n\t\t\t\"element\": {\n\t\t\t\t\"type\": \"plain_text_input\",\n\t\t\t\t\"action_id\": \"plain_text_input-action\"\n\t\t\t},\n\t\t\t\"label\": {\n\t\t\t\t\"type\": \"plain_text\",\n\t\t\t\t\"text\": \"API_KEY\",\n\t\t\t\t\"emoji\": true\n\t\t\t}\n\t\t}\n\t],\n\t\"type\": \"modal\"\n}";

		View view = GsonFactory.createSnakeCase().fromJson(json, View.class);

		slack.methods().viewsOpen(r -> r.token(botToken).triggerId(req.getParameter("trigger_id")).view(view));

	}

	@PostMapping(value = "/test")
	public void getUrl(RequestEntity<String> res, HttpServletResponse response) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_OK);

		String str = res.getBody().substring(8);

		String result = java.net.URLDecoder.decode(str, StandardCharsets.UTF_8.name());

		JSONObject obj = new JSONObject(result);

		JSONObject viewRes = obj.getJSONObject("view").getJSONObject("state").getJSONObject("values");

		JSONObject key = viewRes.getJSONObject("input-url").getJSONObject("plain_text_input-action");

		String enteredUrl = key.get("value").toString();

		JSONObject value = viewRes.getJSONObject("input-key").getJSONObject("plain_text_input-action");

		String enteredKey = value.get("value").toString();

		String url = "https://www.webpagetest.org/runtest.php?url=" + enteredUrl + "&k=" + enteredKey
		// + "&location=" + address.getLocation()
				+ "&fvonly=1&f=json" + "&pingback=" + ngrokUrl + "/testresult";

		restTemplate.getForEntity(url, String.class);

	}

	@GetMapping(value = "/testresult")
	public void getData(HttpServletRequest request) throws IOException, SlackApiException {

		String jsonUrl = "https://www.webpagetest.org/jsonResult.php?test=" + request.getParameter("id");

		ResponseEntity<String> res = restTemplate.getForEntity(jsonUrl, String.class);

		ObjectMapper map = new ObjectMapper();
		try {
			JsonNode printData = map.readTree(res.getBody());
			slackService.sendMsgtoSlack(printData, request);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
