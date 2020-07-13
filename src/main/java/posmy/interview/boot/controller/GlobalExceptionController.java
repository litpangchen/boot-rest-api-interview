package posmy.interview.boot.controller;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import posmy.interview.boot.constants.StatusResponse;
import posmy.interview.boot.vo.ResponseResult;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseResult<?> missingServletRequestHandler(Exception ex) {
        return new ResponseResult<>(StatusResponse.INVALID_OR_MISSING_PARAMETERS);
    }
}
