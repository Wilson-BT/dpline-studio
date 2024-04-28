

package com.dpline.alert.plugin.email.template;

import com.dpline.alert.api.ShowType;

public interface AlertTemplate {

    /**
     * get a message from a specified alert template
     *
     * @param content alert message content
     * @param showType show type
     * @param showAll whether to show all
     * @return a message from a specified alert template
     */
    String getMessageFromTemplate(String content, ShowType showType, boolean showAll);

    /**
     * default showAll is true
     *
     * @param content alert message content
     * @param showType show type
     * @return a message from a specified alert template
     */
    default String getMessageFromTemplate(String content, ShowType showType) {
        return getMessageFromTemplate(content, showType, true);
    }
}
