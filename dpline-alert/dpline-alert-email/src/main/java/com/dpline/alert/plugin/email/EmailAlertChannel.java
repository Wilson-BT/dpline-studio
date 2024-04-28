

package com.dpline.alert.plugin.email;


import java.util.Map;

import com.google.auto.service.AutoService;
import com.dpline.alert.api.AlertData;
import com.dpline.alert.api.AlertInfo;
import com.dpline.alert.api.AlertResult;
import com.dpline.alert.api.AlertToolInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(AlertToolInstance.class)
public final class EmailAlertChannel extends AlertToolInstance {

    private static final Logger log = LoggerFactory.getLogger(EmailAlertChannel.class);

    @Override
    public String name() {
        return "EMAIL";
    }

    @Override
    public AlertResult send(AlertInfo alertInfo) {

        AlertData alert = alertInfo.getAlertData();
        Map<String, String> paramsMap = alertInfo.getAlertParams();
        if (null == paramsMap) {
            return new AlertResult("false", "mail params is null");
        }
        MailSender mailSender = new MailSender(paramsMap);
        AlertResult alertResult = mailSender.sendMails(alert.getTitle(), alert.getContent());

        boolean flag;

        if (alertResult == null) {
            alertResult = new AlertResult();
            alertResult.setStatus("false");
            alertResult.setMessage("alert send error.");
            log.info("alert send error : {}", alertResult.getMessage());
            return alertResult;
        }

        flag = Boolean.parseBoolean(String.valueOf(alertResult.getStatus()));

        if (flag) {
            log.info("alert send success");
            alertResult.setMessage("email send success.");
        } else {
            alertResult.setMessage("alert send error.");
            log.info("alert send error : {}", alertResult.getMessage());
        }

        return alertResult;
    }
}
