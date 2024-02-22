package com.dpline.remote.command;

import com.dpline.common.enums.ResponseStatus;

import java.io.Serializable;

public class IngressAddResponseCommand extends AbstractResponseCommand implements Serializable {

    ResponseStatus status;

}
