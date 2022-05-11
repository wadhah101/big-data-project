package beats;

import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileBeatsDataDeserializer implements DeserializationSchema<FileBeatsData> {
    @Override
    public void open(InitializationContext context) throws Exception {
        DeserializationSchema.super.open(context);
    }

    @Override
    public FileBeatsData deserialize(byte[] bytes) throws IOException {
        String line = new String(bytes, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        return gson.fromJson(line ,FileBeatsData.class );
    }

    @Override
    public void deserialize(byte[] message, Collector<FileBeatsData> out) throws IOException {
        DeserializationSchema.super.deserialize(message, out);
    }

    @Override
    public boolean isEndOfStream(FileBeatsData fileBeatsData) {
        return false;
    }

    @Override
    public TypeInformation<FileBeatsData> getProducedType() {
        return TypeInformation.of(FileBeatsData.class);
    }
}
