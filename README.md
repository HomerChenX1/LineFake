# LineFake
I wrote a program to learn how to write a program as LINE on android.
The test accounts are as following:
  admin@null.com
  owner@null.com
  guest@null.com
  master@null.com
  
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
