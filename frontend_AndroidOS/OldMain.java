package com.alexrappa.myapplication;

/**
 * Created by Alex on 11/28/2016.
 */

public class OldMain {
//
//    public final static String EXTRA_EMAIL = "com.alexrappa.myapplication.EMAIL";
//    public final static String EXTRA_NAME = "com.alexrappa.myapplication.NAME";
//    public final static String EXTRA_MESSAGE = "com.alexrappa.myapplication.MESSAGE";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        updateLog();
//    }
//
//    @Override
//    protected void onStart(){
//        super.onStart();
//        //setContentView(R.layout.activity_main);
//        updateLog();
//    }
//    public String listType = null;
//
//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.listUsers:
//                if (checked)
//                    listType = "user";
//                break;
//            case R.id.listBoards:
//                if (checked)
//                    listType = "board";
//                break;
//            case R.id.listPosts:
//                if (checked)
//                    listType = "post";
//                break;
//            default:
//                listType = null;
//        }
//    }
//
//    /*
//    This method will list DB entities
//    */
//    public void getList(View view){
//        if(listType != null) {
//            Intent intent = new Intent(this, ListEntityActivity.class);     // (context, class)
//            intent.putExtra(EXTRA_MESSAGE, listType);         // key: EXTRA_MESSAGE; value: message
//            startActivity(intent);                           // starts instance of ListEntityActivity
//        }
//    }
//
//    /*
//    This method creates a new user
//    */
//    public void createUser(View view) {
//        Intent intent = new Intent(this, CreateUserActivity.class);     // (context, class)
//        EditText userEmail = (EditText) findViewById(R.id.userEmail);      // gets editText content
//        EditText userName = (EditText) findViewById(R.id.userName);
//        String email = userEmail.getText().toString();
//        String name = userName.getText().toString();
//        if (email.length() >=5 && name.length() >= 3){
//            intent.putExtra(EXTRA_EMAIL, email);         // key: EXTRA_MESSAGE; value: message
//            intent.putExtra(EXTRA_NAME, name);         // key: EXTRA_MESSAGE; value: message
//            startActivity(intent);                           // starts instance of ListEntityActivity
//        }
//    }
//
//    public void updateLog() {
//        byte[] buffer = new byte[1024];
//        FileInputStream inputStream;
//        try{
//            inputStream = openFileInput("action_log");
//            inputStream.read(buffer, 0, 1024);
//            inputStream.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        TextView textView = (TextView) findViewById(R.id.action_log);
//        String string = new String(buffer, StandardCharsets.UTF_8);
//        textView.setText(string);
//    }
}

