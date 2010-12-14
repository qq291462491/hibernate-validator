/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.validator.messageinterpolation;

import java.util.IllegalFormatException;
import java.util.Locale;
import javax.validation.MessageInterpolator;
import javax.validation.ValidationException;


/**
 * A message interpolator which can interpolate the validated value and format this value using the syntax from
 * {@link java.util.Formatter}. Check the <code>Formatter</code> documentation for formatting syntax and options. If no
 * formatting string is specified <code>String.valueOf(validatedValue)}</code> is called.
 * <p/>
 * To interpolate the validated value add <code>&#123;validatedValue&#125;</code> into the message. To specify a format
 * pattern use <code>$&#123;validatedValue:[format string]&#125;</code>, e.g. <code>$&#123;validatedValue:%1$ty&#125;</code>.
 *
 * @author Hardy Ferentschik
 */
public class ValueFormatterMessageInterpolator extends AbstractFormattingMessageInterpolator
		implements MessageInterpolator {
	/**
	 * Construct a message interpolator which delegates
	 * the initial interpolation to the given MessageInterpolator and
	 * uses {@link String#format} for interpolating the validated value.
	 */
	public ValueFormatterMessageInterpolator() {
		this( null );
	}

	/**
	 * Construct a message interpolator which delegates
	 * the initial interpolation to the given MessageInterpolator and
	 * uses {@link String#format} for interpolating the validated value.
	 *
	 * @param userMessageInterpolator the user specified message interpolator
	 */
	public ValueFormatterMessageInterpolator(MessageInterpolator userMessageInterpolator) {
		super( userMessageInterpolator );
	}

	protected String interpolateValidatedValue(String expression, Object validatedValue, Locale locale) {
		String interpolatedValue;
		int separatorIndex = expression.indexOf( VALIDATED_VALUE_FORMAT_SEPARATOR );

		if ( separatorIndex == -1 ) {
			interpolatedValue = String.valueOf( validatedValue );
		}
		else {
			String format = expression.substring( separatorIndex + 1, expression.length() - 1 );
			if ( format.length() == 0 ) {
				throw new ValidationException( "Missing format string in template: " + expression );
			}
			try {
				interpolatedValue = String.format( locale, format, validatedValue );
			}
			catch ( IllegalFormatException e ) {
				throw new ValidationException( "Invalid format: " + e.getMessage(), e );
			}
		}
		return interpolatedValue;
	}
}