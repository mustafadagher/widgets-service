package com.mustafadagher.widgets.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Controller
@RequestMapping("${openapi.widgetsService.base-path:}")
public class WidgetsApiController implements WidgetsApi {

    private final NativeWebRequest request;

    public WidgetsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
