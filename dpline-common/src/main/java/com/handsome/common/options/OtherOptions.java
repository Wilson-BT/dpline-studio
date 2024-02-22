package com.handsome.common.options;

import com.handsome.common.enums.Flag;
import com.handsome.common.enums.ResolveOrderType;
import com.handsome.common.enums.RunModeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtherOptions {

    private Flag openChain;

    private ResolveOrderType resolveOrder;

    private RunModeType runModeType;

    private String deployAddress;
}
