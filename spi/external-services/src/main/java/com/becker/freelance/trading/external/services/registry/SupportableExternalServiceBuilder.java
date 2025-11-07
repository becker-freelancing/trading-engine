package com.becker.freelance.trading.external.services.registry;

public interface SupportableExternalServiceBuilder<PARAMS, SUPPORTSPARAM, SERVICE extends ExternalService> extends ParamsExternalServiceBuilder<PARAMS, SERVICE> {

    public boolean supports(SUPPORTSPARAM supportsparam);
}
