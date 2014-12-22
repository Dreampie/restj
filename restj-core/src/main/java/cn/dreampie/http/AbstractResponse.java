package cn.dreampie.http;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

/**
 * Date: 1/3/14
 * Time: 20:46
 */
public abstract class AbstractResponse<R> implements Response {
  private static final Logger logger = LoggerFactory.getLogger(AbstractResponse.class);

  private final Class<R> responseClass;
  private final R response;

  private HttpStatus status = HttpStatus.OK;
  private Charset charset;
  private PrintWriter writer;
  private OutputStream outputStream;
  private boolean closed;

  // used to store headers set to be able to return them in getHeader()
  private final Map<String, String> headers = Maps.newLinkedHashMap();

  protected AbstractResponse(Class<R> responseClass, R response) {
    this.responseClass = responseClass;
    this.response = response;
  }


  public HttpStatus getStatus() {
    return status;
  }


  public Response setStatus(HttpStatus httpStatus) {
    this.status = httpStatus;
    doSetStatus(httpStatus);
    return this;
  }


  public Response setContentType(String s) {
    if (HTTP.isTextContentType(s)) {
      Optional<String> cs = HTTP.charsetFromContentType(s);
      if (!cs.isPresent()) {
        s += "; charset=UTF-8";
        charset = Charsets.UTF_8;
      } else {
        charset = Charset.forName(cs.get());
      }
    }
    setHeader("Content-Type", s);
    return this;
  }

  public Optional<Charset> getCharset() {
    return Optional.fromNullable(charset);
  }


  public PrintWriter getWriter() throws IOException {
    if (writer != null) {
      return writer;
    }

    if (charset == null) {
      logger.warn("no charset defined while getting writer to write http response." +
          " Make sure you call setContentType() before calling getWriter(). Using UTF-8 charset.");
      charset = Charsets.UTF_8;
    }
    return writer = new PrintWriter(
        new OutputStreamWriter(doGetOutputStream(), charset), true);
  }


  public OutputStream getOutputStream() throws IOException {
    if (outputStream != null) {
      return outputStream;
    }
    return outputStream = doGetOutputStream();
  }


  public void close() throws Exception {
    if (isClosed()) {
      return;
    }
    try {
      if (writer != null) {
        writer.println();
        writer.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }
      closeResponse();
    } finally {
      closed = true;
    }
  }


  public boolean isClosed() {
    return closed;
  }


  public Response addCookie(String cookie, String value) {
    addCookie(cookie, value, Duration.ZERO);
    return this;
  }


  public final Response setHeader(String headerName, String header) {
    doSetHeader(headerName, header);
    headers.put(headerName.toLowerCase(Locale.ENGLISH), header);
    return this;
  }

  protected abstract void doSetHeader(String headerName, String header);


  public Optional<String> getHeader(String headerName) {
    return Optional.fromNullable(headers.get(headerName.toLowerCase(Locale.ENGLISH)));
  }


  public String toString() {
    return "[RESTJ RESPONSE] " + status;
  }


  public <T> T unwrap(Class<T> clazz) {
    if (clazz == this.responseClass) {
      return (T) response;
    }
    throw new IllegalArgumentException("underlying implementation is " + this.responseClass.getName()
        + ", not " + clazz.getName());
  }

  protected abstract void closeResponse() throws IOException;

  protected abstract OutputStream doGetOutputStream() throws IOException;

  protected abstract void doSetStatus(HttpStatus httpStatus);
}
