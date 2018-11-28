# LineFake
  I wrote a program to learn how to write a program as LINE on android.
  The program is implemented by ArrayList, SQLite or FireBase.
    DbHelper.useSQL == 0 means implementation of ArrayList.
    DbHelper.useSQL == 1 means implementation of SQLite.
    DbHelper.useSQL == 2 means implementation of FireBase.
  Missing the messaging system, the implementations of ArrayList and SQLite
only simulate the two partys' chating.
  But the implementation of FireBase can actually chat.

The test accounts are as following:
  admin@null.com pw:111111
  owner@null.com pw:222222
  guest@null.com pw:333333
  master@null.com pw:444444
  
MainActivity.java:
    As a login interface. Use TextInputLayout + AutoCompleteTextView + ProgressBar.
InfoActivity.java
    As an introduction interface. Use ListView.
MemberActivity.java
    As a member update/delete/register interface. Only use EditText.
FriendsActivity.java+FriendAdapter.java
    As the friend add/delete/query interface. Use ListView + ContextMenu.
ChannelActivity.java+ChnAdapter.java
    The chat add/delete interface. Use RecyclerView + PopupMenu.
DbHelper.java
    The database interface. Implements by ArrayList.
SqlDbHelper.java 
    The database interface. Implements by SQLite.
MemberWithFriend.java
    Classes with Member, MemberWithFriend, Friend and Chat.
FireDbHelper.java
    The FireBase interface.
CheckEnd.java
    The AsyncTask interface. Supported for FireBase.
EbusEvent.java
    The GreenRoBot EventBus interface. Supported for FireBase.
MyApplication.java
    For debug used.
