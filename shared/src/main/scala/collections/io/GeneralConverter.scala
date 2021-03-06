/*
 * product-collections is distributed under the 2-Clause BSD license. See the
 * LICENSE file in the root of the repository.
 *
 * Copyright (c) 2013 - 2015 Mark Lister
 */


package com.github.marklister.collections.io

import scala.annotation.implicitNotFound
import scala.util.Try

/**
 * A General Converter is a wrapper for a function (String)=>T
 *
 * This function is used by the CsvParser to parse fields of type T
 *
 * There are several default converters defined or you can make your own
 * and make them available as implict parameters.
 *
 * The reason we use a wrapper is to avoid any old (String)=>T function
 * being selected by mistake.  If you made a GeneralConverter then you 
 * almost certainly intended that the parser use it to convert type T.
 *
 * Example:{{{
 *   implicit object ymd extends GeneralConverter[java.util.Date]{
 *     val f= new java.text.SimpleDateFormat("yy-MM-dd")
 *     def convert(x:String)=f.parse(x.trim)
 *   }
 * }}}
 *
 */
@implicitNotFound( """No converter found for String => ${A}
          You can define your own converters:
          see https://github.com/marklister/product-collections#examples""")
class GeneralConverter[A](val converter: String => A) {
  def apply(s: String) = converter(s)
  def toOption = new GeneralConverter(x => Try(converter(x)).toOption)
}

/**
 * The companion class for GeneralConverter.  It contains several default
 * converters used by [[com.github.marklister.collections.io.CsvParser]]
 */
object GeneralConverter {

  implicit val StringConverter: GeneralConverter[String] = new GeneralConverter(identity(_))
  implicit val IntConverter: GeneralConverter[Int] = new GeneralConverter(_.trim.toInt)
  implicit val LongConverter: GeneralConverter[Long] = new GeneralConverter(_.trim.toLong)
  implicit val ByteConverter: GeneralConverter[Byte] = new GeneralConverter(_.trim.toByte)
  implicit val ShortConverter: GeneralConverter[Short] = new GeneralConverter(_.trim.toShort)
  implicit val FloatConverter: GeneralConverter[Float] = new GeneralConverter(_.trim.toFloat)
  implicit val DoubleConverter: GeneralConverter[Double] = new GeneralConverter(_.trim.toDouble)
  implicit val BooleanConverter: GeneralConverter[Boolean] = new GeneralConverter(_.trim.toBoolean)
  implicit val OptionStringConverter = new GeneralConverter(s=> if (s.isEmpty) None else Some(s))
  implicit val OptionIntConverter: GeneralConverter[Option[Int]] = IntConverter.toOption
  implicit val OptionDoubleConverter = DoubleConverter.toOption
  implicit val OptionLongConverter = LongConverter.toOption
  implicit val OptionBooleanConverter = BooleanConverter.toOption
}
