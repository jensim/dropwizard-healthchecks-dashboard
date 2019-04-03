package testinggrounds.micronaut.healthweb.repo

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import java.net.URL
import javax.inject.Singleton

@Singleton
class UrlToStringTypeConverter : Codec<URL> {

    override fun getEncoderClass(): Class<URL> = URL::class.java

    override fun encode(writer: BsonWriter, value: URL, encoderContext: EncoderContext) {
        writer.writeString(value.toString())
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): URL = URL(reader.readString())
}
