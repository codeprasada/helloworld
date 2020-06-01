package com.example.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.helloworld.beans.ModemData;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import java.io.IOException;

@SpringBootApplication
public class HelloworldApplication {

	@Value("${TARGET:World}")
	String target;

	private String tableId;
	private String instanceId;
	private String projectId;
	private BigtableDataClient dataClient;

	@RestController
	class HelloworldController {

		@GetMapping("/{id}")
		ModemData hello(@PathVariable("id") String id) {
			
			//initialize pojo
			ModemData modemdata = new ModemData();
			
			//Get the Big Table settings from environment variables
			tableId = System.getenv("tableid");
			instanceId = System.getenv("instanceid");
			projectId = System.getenv("projectid");

			//default values for local testing --> Plz ignore thos section
			if (tableId == null)tableId = "demo2";	
			if (instanceId == null) instanceId = "testdatabase";
			if (projectId == null) projectId = "mypocdata";	

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

			//Read a row by key (Key could be subscriber id or imesv no
			Row row = dataClient.readRow(tableId, id);

			//If row is read, populate the object
			if ( row != null) {

				for (RowCell cell : row.getCells()) {

					switch(cell.getQualifier().toStringUtf8()) {

					case "cal_timestamp_time":
						modemdata.setCal_timestamp_time(cell.getValue().toStringUtf8());
						break;

					case "cell_id":
						modemdata.setCell_id(cell.getValue().toStringUtf8());	
						break;

					case "subscriber_id":
						modemdata.setSubscriber_id(cell.getValue().toStringUtf8());
						break;

					case "userequipment_imeisv":
						modemdata.setUserequipment_imeisv(cell.getValue().toStringUtf8());
						break;

					case "cell_latitude":
						modemdata.setCell_latitude(cell.getValue().toStringUtf8());
						break;

					case "cell_longitude":
						modemdata.setCell_longitude(cell.getValue().toStringUtf8());
						break;

					case "subscriber_msisdn":
						modemdata.setSubscriber_msisdn(cell.getValue().toStringUtf8());
						break;

					default:
						//continue;
					}
				}
			};
			
			//Close BigTable client
			dataClient.close();
			return modemdata;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloworldApplication.class, args);
	}
}