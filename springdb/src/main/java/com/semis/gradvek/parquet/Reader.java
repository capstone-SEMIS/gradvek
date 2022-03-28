package com.semis.gradvek.parquet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * the utility class for reading Parquet-formatted files using Hadoop internals
 * instead of full-blown Sparc
 * @author ymachkasov
 *
 */
public class Reader {

	public static Parquet read (java.nio.file.Path filePath) throws IOException {
		List<Group> groups = new ArrayList<> ();

		try (ParquetFileReader reader = ParquetFileReader.open (
				HadoopInputFile.fromPath (new Path (filePath.toRealPath ().toString ()), new Configuration ())
				)
			)
		{
			MessageType schema = reader.getFooter ().getFileMetaData ().getSchema ();

			for (PageReadStore pages = reader.readNextRowGroup (); pages != null; pages = reader.readNextRowGroup ()) {
				long rows = pages.getRowCount ();
				MessageColumnIO columnIO = new ColumnIOFactory ().getColumnIO (schema);
				RecordReader<Group> recordReader = columnIO.getRecordReader (pages, new GroupRecordConverter (schema));

				for (int iRow = 0; iRow < rows; ++iRow) {
					groups.add (recordReader.read ());
				}
			}
			
			return new Parquet (groups, schema.getFields ());
		}
	}
}