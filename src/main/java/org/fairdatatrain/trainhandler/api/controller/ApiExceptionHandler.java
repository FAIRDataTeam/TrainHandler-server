/**
 * The MIT License
 * Copyright © 2022 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatatrain.trainhandler.api.controller;

import jakarta.validation.ValidationException;
import org.fairdatatrain.trainhandler.api.dto.error.ErrorDTO;
import org.fairdatatrain.trainhandler.exception.CannotPerformException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.exception.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.lang.String.format;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<ErrorDTO> handleNotImplementedException(
            NotImplementedException exception) {
        return new ResponseEntity<>(
                new ErrorDTO("HTTP-501", "Not yet implemented."), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(
                new ErrorDTO(
                        "HTTP-404",
                        format(
                                "Cannot find entity %s with %s",
                                exception.getEntityName(), exception.getFields()
                        )
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CannotPerformException.class)
    public ResponseEntity<ErrorDTO> handleCannotDeleteException(CannotPerformException exception) {
        return new ResponseEntity<>(
                new ErrorDTO(
                        "HTTP-400-DeletionError",
                        format(
                                "Cannot perform %s on entity %s (with %s)",
                                exception.getOperation(),
                                exception.getEntityName(),
                                exception.getFields()
                        )
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(ValidationException exception) {
        return new ResponseEntity<>(
                new ErrorDTO(
                        "HTTP-400-ValidationError",
                        exception.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
