package com.springboot.covid19trackingapp.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.springboot.covid19trackingapp.models.LocationStat;

@Service
public class Covid19DataService {
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<LocationStat> allLocationStats = new ArrayList<LocationStat>();

	public List<LocationStat> getAllLocationStats() {
		return allLocationStats;
	}

	@PostConstruct
	@Scheduled(cron = "0 0 0 * * ?") // Everyday at midnight 12am
	public void fetchVirusData() throws IOException, InterruptedException {

		List<LocationStat> allLocationStats = new ArrayList<LocationStat>();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

		StringReader csvReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
		for (CSVRecord record : records) {
			LocationStat locationStat = new LocationStat();
			locationStat.setState(record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));

			int latestTotalCases = Integer.parseInt(record.get(record.size() - 1));
			int prevDayTotalCases = Integer.parseInt(record.get(record.size() - 2));
			int diffFromPrevDay = latestTotalCases - prevDayTotalCases;
			locationStat.setLatestTotalCases(latestTotalCases);
			locationStat.setDiffFromPrevDay(diffFromPrevDay);
			allLocationStats.add(locationStat);
		}
		this.allLocationStats = allLocationStats;
//		System.out.println(this.allLocationStats);
	}
}
