/***************************************************************************
 * Copyright 2017 Lead Wire (http://leadwire-apm.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.monitoring.probe.aspectj.operationExecution;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Wassim Dhib
 * 
 * @since 1.13
 */ 


public class HtmlResponseWrapper extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream capture;
	private ServletOutputStream output;
	private PrintWriter writer;

	public HtmlResponseWrapper(HttpServletResponse rep) {		
		super(rep);
		capture = new ByteArrayOutputStream(rep.getBufferSize());
	}

	@Override
	public ServletOutputStream getOutputStream() {
		if (writer != null) {
			throw new IllegalStateException(
					"getWriter() has already been called on this response.");
	}

	if (output == null) {
		output = new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {
				capture.write(b);
			}

			@Override
			public void flush() throws IOException {
				capture.flush();
			}

			@Override
			public void close() throws IOException {
				capture.close();
			}
		};
	}

	return output;
}

@Override
public PrintWriter getWriter() throws IOException {
	if (output != null) {
		throw new IllegalStateException(
				"getOutputStream() has already been called on this response.");
		}

		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(capture,
					getCharacterEncoding()));
		}

		return writer;
	}

	@Override
	public void flushBuffer() throws IOException {
		super.flushBuffer();

		if (writer != null) {
			writer.flush();
		} else if (output != null) {
			output.flush();
		}
	}

	public byte[] getCaptureAsBytes() throws IOException {
		if (writer != null) {
			writer.close();
		} else if (output != null) {
			output.close();
		}

		return capture.toByteArray();
	}

	public String getCaptureAsString() throws IOException {
		return new String(getCaptureAsBytes(), getCharacterEncoding());
	}

}
