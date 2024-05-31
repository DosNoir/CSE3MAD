import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.example.cse3masassignment.DBfiller;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

// Use RobolectricTestRunner to run the tests with Robolectric
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DBfillerTest {
    @Mock
    Context mockContext;

    @Mock
    AssetManager mockAssetManager;

    @Mock
    Resources mockResources;

    @Mock
    FirebaseFirestore mockDb;

    @Mock
    DocumentReference mockDocumentReference;

    @Mock
    CollectionReference mockCollectionReference;

    DBfiller dbfiller;

    @Before
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Mock FirebaseApp initialization
        FirebaseApp.initializeApp(RuntimeEnvironment.application);
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockContext.getResources()).thenReturn(mockResources);

        // Inject the mocked Firestore instance
        dbfiller = new DBfiller(mockContext);
        dbfiller.db = mockDb;
    }

    @Test
    public void testReadCSVFromAssets_Success() throws Exception {
        String csvData = "eventID,eventName,eventDate,eventLocation,latitude,longitude\n" +
                "1,Concert,2022-05-15,Stadium,12.34,56.78\n" +
                "2,Festival,2022-06-20,Park,23.45,67.89";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        when(mockAssetManager.open("events.csv")).thenReturn(inputStream);

        List<String[]> result = dbfiller.readCSVFromAssets("events.csv");

        assertNotNull(result);
        assertEquals(3, result.size()); // Including header row
        assertArrayEquals(new String[]{"1", "Concert", "2022-05-15", "Stadium", "12.34", "56.78"}, result.get(1));
    }

    @Test
    public void testReadCSVFromAssets_FileNotFound() throws Exception {
        when(mockAssetManager.open("nonexistent.csv")).thenThrow(new java.io.FileNotFoundException());

        List<String[]> result = dbfiller.readCSVFromAssets("nonexistent.csv");

        assertNull(result);
    }

    @Test
    public void testReadCSVFromAssets_Exception() throws Exception {
        when(mockAssetManager.open("events.csv")).thenThrow(new java.io.IOException());

        List<String[]> result = dbfiller.readCSVFromAssets("events.csv");

        assertNull(result);
    }

    @Test
    public void testAddEvent() {
        String[] eventRow = {"0", "eventID1", "Concert", "2022-05-15", "Stadium", "12.34", "56.78"};

        when(mockDb.collection("Events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("eventID1")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null));

        dbfiller.addEvent(eventRow);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDocumentReference).set(captor.capture());

        Map<String, Object> capturedEvent = captor.getValue();
        assertEquals("Concert", capturedEvent.get("eventName"));
        assertEquals("2022-05-15", capturedEvent.get("eventDate"));
        assertEquals("Stadium", capturedEvent.get("eventLocation"));
        assertEquals(new GeoPoint(12.34, 56.78), capturedEvent.get("eventGeoPoint"));
    }
}
