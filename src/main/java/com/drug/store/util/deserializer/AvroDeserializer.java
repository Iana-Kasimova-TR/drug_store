package com.drug.store.util.deserializer;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
@RequiredArgsConstructor
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

  protected final Class<T> targetType;

  @Override
  public T deserialize(String topic, byte[] data) {
    if (data != null) {
      sendDebugLog("data='{}'", Hex.encodeHexString(data));
      try {
        DatumReader<GenericRecord> datumReader =
            new SpecificDatumReader<>(
                targetType.getDeclaredConstructor().newInstance().getSchema());
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);

        T result = (T) datumReader.read(null, decoder);

        sendDebugLog("deserialized data='{}'", result.toString());
        return result;

      } catch (Exception e) {
        throw new SerializationException(
            "Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", e);
      }
    } else {
      throw new SerializationException("Data from message is empty ");
    }
  }

  private void sendDebugLog(String value, String... args) {
    if (log.isDebugEnabled()) {
      log.debug(value, args);
    }
  }
}
