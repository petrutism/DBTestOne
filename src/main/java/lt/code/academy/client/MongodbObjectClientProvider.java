package lt.code.academy.client;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lt.code.academy.ApplicationProperties;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongodbObjectClientProvider {
    private static MongoClient client;

    private MongodbObjectClientProvider() {
        CodecRegistry registry = CodecRegistries.
                fromProviders(PojoCodecProvider.builder()
                        .automatic(true).build());

        CodecRegistry codecRegistry = CodecRegistries
                .fromRegistries(MongoClientSettings
                        .getDefaultCodecRegistry(), registry);

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyConnectionString(new ConnectionString(ApplicationProperties.getInstance().getValue("jdbc.mongodb.connection.url")))
                .build();

        client = MongoClients.create(settings);

    }

    public static MongoClient getClient() {
        if (client == null) {
            new MongodbObjectClientProvider();
        }

        return client;
    }
}
