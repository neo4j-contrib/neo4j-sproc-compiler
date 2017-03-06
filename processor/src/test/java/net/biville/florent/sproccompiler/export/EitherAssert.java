/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.biville.florent.sproccompiler.export;

import org.assertj.core.api.AbstractAssert;

import java.util.function.Consumer;

public class EitherAssert<L, R> extends AbstractAssert<EitherAssert<L,R>,Either<L,R>>
{
    private EitherAssert( Either<L,R> actual )
    {
        super( actual, EitherAssert.class );
    }

    public static <L, R> EitherAssert<L,R> assertThat( Either<L,R> actual )
    {
        return new EitherAssert<>( actual );
    }

    public EitherAssert<L,R> isLeft()
    {
        isNotNull();

        actual.consume( ( L left ) ->
        {
        }, ( R right ) ->
        {
            failWithMessage( "Expected Either to be left, but right is defined: %s", right );
        } );

        return this;
    }

    public EitherAssert<L,R> isRight()
    {
        isNotNull();

        actual.consume( ( L left ) ->
        {
            failWithMessage( "Expected Either to be right, but left is defined: %s", left );
        }, ( R right ) ->
        {
        } );

        return this;
    }

    public EitherAssert<L,R> verifiesLeft( Consumer<L> leftConsumer )
    {
        isLeft();

        actual.consume( leftConsumer, ( right ) ->
        {
        } );

        return this;
    }

    public EitherAssert<L,R> verifiesRight( Consumer<R> rightConsumer )
    {
        isRight();

        actual.consume( ( left ) ->
        {
        }, rightConsumer );

        return this;
    }
}
