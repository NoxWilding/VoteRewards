package me.shroomz.voterewards.Migrate;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shroomz.voterewards.Voterewards;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class VoteJsonFileMigration implements Migration {
    private final JsonParser parser = new JsonParser();

    @Override
    public String getName() {
        return "Voterewards JSON storage";
    }

    @Override
    public void execute(ProgressListener listener) {
        File file = new File(Voterewards.getPlugin().getDataFolder(), Voterewards.getPlugin().getConfig().getString("storage.json.file"));
        JsonObject object;
        try (BufferedReader r = Files.newReader(file, StandardCharsets.UTF_8)) {
            object = parser.parse(r).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Unable to open Voterewards JSON file " + file, e);
        }

        // v1 format or v2?
        int records;
        int divisor;
        if (object.has("version")) {
            // v2.
            JsonObject recordObj = object.getAsJsonObject("records");
            records = recordObj.entrySet().size();
            divisor = ProgressUtil.findBestDivisor(records);
            listener.onStart(records);
            int currentIdx = 0;
            for (Map.Entry<String, JsonElement> entry : recordObj.entrySet()) {
                JsonObject o = entry.getValue().getAsJsonObject();

                int votes = o.getAsJsonPrimitive("votes").getAsInt();
                long lastVoted = o.getAsJsonPrimitive("lastVoted").getAsLong();
                Voterewards.getPlugin().getVoteStorage().setVotes(UUID.fromString(entry.getKey()), votes, lastVoted);
                currentIdx++;

                if (currentIdx % divisor == 0) {
                    listener.onRecordBatch(currentIdx, records);
                }
            }
        } else {
            // v1.
            records = object.entrySet().size();
            divisor = ProgressUtil.findBestDivisor(records);
            listener.onStart(records);
            int currentIdx = 0;
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                int votes = entry.getValue().getAsInt();
                Voterewards.getPlugin().getVoteStorage().setVotes(UUID.fromString(entry.getKey()), votes);
                currentIdx++;

                if (currentIdx % divisor == 0) {
                    listener.onRecordBatch(currentIdx, records);
                }
            }
        }

        listener.onFinish(records);
    }
}
