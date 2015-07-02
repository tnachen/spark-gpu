/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.catalyst.expressions

import org.apache.spark.sql.catalyst.expressions.codegen.{CodeGenContext, GeneratedExpressionCode}
import org.apache.spark.sql.types._

/**
 * Return the unscaled Long value of a Decimal, assuming it fits in a Long.
 * Note: this expression is internal and created only by the optimizer,
 * we don't need to do type check for it.
 */
case class UnscaledValue(child: Expression) extends UnaryExpression {

  override def dataType: DataType = LongType
  override def toString: String = s"UnscaledValue($child)"

  override def eval(input: InternalRow): Any = {
    val childResult = child.eval(input)
    if (childResult == null) {
      null
    } else {
      childResult.asInstanceOf[Decimal].toUnscaledLong
    }
  }

  override def genCode(ctx: CodeGenContext, ev: GeneratedExpressionCode): String = {
    defineCodeGen(ctx, ev, c => s"$c.toUnscaledLong()")
  }
}

/**
 * Create a Decimal from an unscaled Long value.
 * Note: this expression is internal and created only by the optimizer,
 * we don't need to do type check for it.
 */
case class MakeDecimal(child: Expression, precision: Int, scale: Int) extends UnaryExpression {

  override def dataType: DataType = DecimalType(precision, scale)
  override def toString: String = s"MakeDecimal($child,$precision,$scale)"

  override def eval(input: InternalRow): Decimal = {
    val childResult = child.eval(input)
    if (childResult == null) {
      null
    } else {
      new Decimal().setOrNull(childResult.asInstanceOf[Long], precision, scale)
    }
  }

  override def genCode(ctx: CodeGenContext, ev: GeneratedExpressionCode): String = {
    val eval = child.gen(ctx)
    eval.code + s"""
      boolean ${ev.isNull} = ${eval.isNull};
      ${ctx.decimalType} ${ev.primitive} = null;

      if (!${ev.isNull}) {
        ${ev.primitive} = (new ${ctx.decimalType}()).setOrNull(
          ${eval.primitive}, $precision, $scale);
        ${ev.isNull} = ${ev.primitive} == null;
      }
      """
  }
}