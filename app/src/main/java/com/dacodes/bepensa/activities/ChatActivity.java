package com.dacodes.bepensa.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.adapters.RecyclerViewChatAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.Chat.ChatEntity;
import com.dacodes.bepensa.models.ChatResponseModel;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.adapters.RecyclerViewChatAdapter.CHAT_BEPENSA_ADMIN;
import static com.dacodes.bepensa.adapters.RecyclerViewChatAdapter.CHAT_BEPENSA_USER;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_CHAT;

public class ChatActivity extends AppCompatActivity implements
        RetrofitWebServices.DefaultResponseListeners{

    private static final String LOG_TAG = ChatActivity.class.getSimpleName();
    /**
     * Duration of wait
     **/

    private Context mContext;
    AppController app;
    private String TAG = ChatActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerViewChatAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    ArrayList<ChatEntity> chatEntities = new ArrayList<>();
    @BindView(R.id.progress_bar)ProgressBar progress_bar;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.send_bt)
    ImageButton send_bt;
    @BindView(R.id.chat_input_et)
    EditText chat_input_et;

    Activity activity;

    //PAGINATION
    public int PAGE_SIZE = 0;
    public void setPAGE_SIZE(int PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
    }
    private boolean isLoading = false;
    private boolean isLastPage = false;
    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }
    private String pagination_next;
    RetrofitWebServices retrofitWebServices;
    private int opportunityId;
    @BindView(R.id.toolbar)Toolbar toolbar;
    private WebSocketClient mWebSocketClient;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        setContentView(R.layout.chat_activity_layout);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        app = (AppController)getApplication();
        activity = this;
        app = (AppController) getApplication();
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        opportunityId = getIntent().getIntExtra("opportunityId", 0);
        toolbar.setTitle("TICKET #"+opportunityId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setReverseLayout(true);
       // mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewChatAdapter(mContext);
        mRecyclerView.addOnScrollListener(scrollListener);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagination_next = null;
                RecyclerViewChatAdapter recyclerViewChatAdapter = (RecyclerViewChatAdapter) mAdapter;
                recyclerViewChatAdapter.clearItems();
                recyclerViewChatAdapter.notifyDataSetChanged();
                retrofitWebServices.getChat(null, app.getAccessToken(), opportunityId);
            }
        });

        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(ChatActivity.this);
                if(!chat_input_et.getText().toString().contentEquals("")) {
                    String text = "{\n" +
                            "   \"text\": \""+chat_input_et.getText()+"\"\n" +
                            "}";
                    try{
                        mWebSocketClient.send(text);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    chat_input_et.setText(null);
                }
            }
        });
        retrofitWebServices.getChat(null, app.getAccessToken(), opportunityId);
        connectChatSocket();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    private void connectChatSocket() {
        String userId = app.getUserId();
        String url = "wss://" + getString(R.string.socket_url)+ "ws/conversation/"+ String.valueOf(opportunityId) + "/" + userId +"/";
        URI uri = getUriForSocket(url);
        if(uri == null) return;

        if(mWebSocketClient!=null){
            mWebSocketClient.close();
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Opened connectDeliveryManWebSocket");
            }

            @Override
            public void onMessage(String s) {
                onChatSocketMessageReceived(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e("Websocket", "Closed connectDeliveryManWebSocket " + s);
                Log.e("Websocket", "CODE " + i);
                if(i != 1000){
                    connectChatSocket();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Websocket", "Error " + e.getMessage());//TODO aquí se loopea
            }
        };


        mWebSocketClient.connect();
    }

    private URI getUriForSocket(String url){
        URI uri;
        try {
            uri = new URI(url);
            Log.d(LOG_TAG, "uri: " + uri.toString());
            return uri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onChatSocketMessageReceived(final String message){
        Gson gson = new Gson();
        ChatEntity chatEntity = gson.fromJson(message,ChatEntity.class);
        //addChatItem(chatEntity);
        addNewChatMessage(chatEntity);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int findLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + findLastVisibleItemPosition) >= totalItemCount
                        && findLastVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {

                    loadMoreItems();

                }
            }

        }
    };

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            retrofitWebServices.getChat(pagination_next, app.getAccessToken(), opportunityId);
        }
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_CHAT:
                isLoading = false;
                ChatResponseModel responseModel = (ChatResponseModel) object;
                if(responseModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                progress_bar.setVisibility(View.GONE);
                ChatResponseModel response = (ChatResponseModel) object;
                //Collections.reverse(responseModel.getData());
                for(int i = 0; i<response.getData().size(); i++){
                    addChatItem(response.getData().get(i));
                }
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    private void addChatItem(ChatEntity chatEntity){
        if(chatEntity.getUser().getUuid().contentEquals(app.getUserId())){
            chatEntity.setType_model(CHAT_BEPENSA_USER);
            mAdapter.addBepensaUserChat(chatEntity);
        }else{
            chatEntity.setType_model(CHAT_BEPENSA_ADMIN);
            mAdapter.addBepensaAdminChat(chatEntity);
        }
    }


    private void addPreviousChatMessage(ChatEntity chatEntity){
        if(chatEntity.getUser().getUuid().contentEquals(app.getUserId())){
            chatEntity.setType_model(CHAT_BEPENSA_USER);
        }else{
            chatEntity.setType_model(CHAT_BEPENSA_ADMIN);
        }
        mAdapter.getItems().add(0, chatEntity);
    }

    private void addNewChatMessage(ChatEntity chatEntity){
        if(chatEntity.getUser().getUuid().contentEquals(app.getUserId())){
            chatEntity.setType_model(CHAT_BEPENSA_USER);
            mAdapter.getItems().add(0,chatEntity);
        }else{
            chatEntity.setType_model(CHAT_BEPENSA_ADMIN);
            mAdapter.getItems().add(0,chatEntity);
        }
        mAdapter.notifyItemInserted(0);
        mAdapter.notifyItemRangeChanged(0, mAdapter.getItems().size());
    }

    @Override
    public void onFailError(int id, int status, String message) {

    }

    @Override
    public void onWebServiceStart() {

    }

    @Override
    public void onWebServiceEnd() {

    }

    @Override
    public void onExpireToken() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebSocketClient!=null)
            mWebSocketClient.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}