package simplediff;

import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.web.servlet.error.ErrorController;
import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorPageController implements ErrorController {

  @RequestMapping(value = "/error")
  @ResponseBody
  public String handleError(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
    Throwable rootException = NestedExceptionUtils.getMostSpecificCause(exception);
    return String.format("<html><head></head><body><h1>An Error Has Occured</h1>"
                         + "Status Code: <b>%s</b><br>"
                         + "Exception Message: <b>%s</b><br>"
                         + "<details><summary>Stack Trace:</summary><pre>%s</pre></details></body></html>",
                         statusCode == null ? -1 : statusCode,
                         rootException==null
                         ? "N/A"
                         : rootException.getMessage(),
                         rootException==null
                         ? "N/A"
                         : ExceptionUtils.getStackTrace(rootException));
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
