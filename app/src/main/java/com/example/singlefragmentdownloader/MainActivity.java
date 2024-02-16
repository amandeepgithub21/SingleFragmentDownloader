package com.example.singlefragmentdownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



    import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Environment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import androidx.fragment.app.Fragment;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        EditText editTextLink = findViewById(R.id.edittext);
        Button buttonDownload = findViewById(R.id.buttondownload);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadLink = editTextLink.getText().toString();
                new DownloadTask().execute(downloadLink);
            }
        });

    }

    private class DownloadTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "download start", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String downloadLink = params[0];

            try {
                // Open a connection to the URL
                URL url = new URL(downloadLink);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Get the content type
                String contentType = connection.getContentType();

                // Determine the file extension based on content type
                String fileExtension = getFileExtension(contentType);

                // Create a file in the appropriate directory
                File directory;
                if (contentType.startsWith("image")) {
                    directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    //Toast.makeText( getContext(), "Create for image", Toast.LENGTH_SHORT ).show();
                } else if (contentType.startsWith("audio")) {
                    directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    //Toast.makeText( getContext(), "Create for audio", Toast.LENGTH_SHORT ).show();
                } else if (contentType.startsWith("video")) {
                    directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    // Toast.makeText( getContext(), "Create for video", Toast.LENGTH_SHORT ).show();
                } else {
                    // Handle other types or default to Downloads directory
                    directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                }
                String filename = getFileNameFromUrl( downloadLink );
                File outputFile = new File(directory, filename);

                // Download and save the file
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }

                // Close streams
                output.close();
                input.close();

                // Disconnect the HttpURLConnection
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(MainActivity.this, "download complete", Toast.LENGTH_SHORT).show();
        }

        private String getFileNameFromUrl(String url) {
            try {
                URL parsedUrl = new URL( url );
                String path = parsedUrl.getPath();
                return path.substring( path.lastIndexOf( '/' ) + 1 );
            } catch (MalformedURLException e) {
                e.printStackTrace(); // Handle the exception according to your needs
                return "default_filename"; // Provide a default name if URL is not valid
            }
        }
        // Helper method to extract file extension from content type
        private String getFileExtension(String contentType) {
            switch (contentType) {
                case "image/jpeg":
                    return "jpg";
                case "image/png":
                    return "png";
                case "audio/mpeg":
                    return "mp3";
                case "video/mp4":
                    return "mp4";
                default:
                    return "unknown";
            }
        }

  /* @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate( values );
            Void progress = values[0];
            progressBar.setProgress( progress );
        }*/
    }
}