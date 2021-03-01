package com.intellij.lang.jsgraphql.types.scalar;

import com.intellij.lang.jsgraphql.types.Internal;
import com.intellij.lang.jsgraphql.types.language.IntValue;
import com.intellij.lang.jsgraphql.types.schema.Coercing;
import com.intellij.lang.jsgraphql.types.schema.CoercingParseLiteralException;
import com.intellij.lang.jsgraphql.types.schema.CoercingParseValueException;
import com.intellij.lang.jsgraphql.types.schema.CoercingSerializeException;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.intellij.lang.jsgraphql.types.scalar.CoercingUtil.isNumberIsh;
import static com.intellij.lang.jsgraphql.types.scalar.CoercingUtil.typeName;

@Internal
public class GraphqlIntCoercing implements Coercing<Integer, Integer> {

    private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);

    private Integer convertImpl(Object input) {
        if (input instanceof Integer) {
            return (Integer) input;
        } else if (isNumberIsh(input)) {
            BigDecimal value;
            try {
                value = new BigDecimal(input.toString());
            } catch (NumberFormatException e) {
                return null;
            }
            try {
                return value.intValueExact();
            } catch (ArithmeticException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Integer serialize(Object input) {
        Integer result = convertImpl(input);
        if (result == null) {
            throw new CoercingSerializeException(
                    "Expected type 'Int' but was '" + typeName(input) + "'."
            );
        }
        return result;
    }

    @Override
    public Integer parseValue(Object input) {
        Integer result = convertImpl(input);
        if (result == null) {
            throw new CoercingParseValueException(
                    "Expected type 'Int' but was '" + typeName(input) + "'."
            );
        }
        return result;
    }

    @Override
    public Integer parseLiteral(Object input) {
        if (!(input instanceof IntValue)) {
            throw new CoercingParseLiteralException(
                    "Expected AST type 'IntValue' but was '" + typeName(input) + "'."
            );
        }
        BigInteger value = ((IntValue) input).getValue();
        if (value.compareTo(INT_MIN) < 0 || value.compareTo(INT_MAX) > 0) {
            throw new CoercingParseLiteralException(
                    "Expected value to be in the Integer range but it was '" + value.toString() + "'"
            );
        }
        return value.intValue();
    }
}
