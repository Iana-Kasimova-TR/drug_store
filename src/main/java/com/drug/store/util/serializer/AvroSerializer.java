package com.drug.store.util.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {

  @Override
  public byte[] serialize(String topic, T data) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byte[] result = null;

      if (data != null) {
        sendDebugLog("data='{}'", data.toString());

        BinaryEncoder binaryEncoder =
            EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(data.getSchema());
        datumWriter.write(data, binaryEncoder);

        binaryEncoder.flush();

        result = byteArrayOutputStream.toByteArray();
        sendDebugLog("serialized data='{}'", Hex.encodeHexString(result));
      }
      return result;
    } catch (IOException ex) {
      throw new SerializationException(
          "Can't serialize data='" + data + "' for topic='" + topic + "'", ex);
    }
  }

  private void sendDebugLog(String value, String... args){
    if(log.isDebugEnabled()){
      log.debug(value, args);
    }
  }
}