package com.example.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import java.io.IOException;

@SpringBootApplication
public class HelloworldApplication {

	@Value("${TARGET:World}")
	String target;

	private final String tableId = "demo2";
	private final String instanceId = "testdatabase";
	private final String projectId = "mypocdata";
	private BigtableDataClient dataClient;

	@RestController
	class HelloworldController {
		@GetMapping("/")
		String hello() {

			// Creates the settings to configure a bigtable data client.
			BigtableDataSettings settings = BigtableDataSettings.newBuilder().setProjectId(projectId)
					.setInstanceId(instanceId).build();
			
		    // Creates a bigtable data client.
		    try {
				dataClient = BigtableDataClient.create(settings);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    String response = "start read";
		    Row row = dataClient.readRow(tableId, "900001");
		    response = response + row.getKey().toStringUtf8();
		    for (RowCell cell : row.getCells()) {
		    	response = response + cell.getQualifier().toStringUtf8() + cell.getValue().toStringUtf8();
		    }
            dataClient.close();
			return response;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloworldApplication.class, args);
	}
}