//package nl.frankkie.baxy2.databaserows;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//
//import flexjson.JSON;
//import nl.frankkie.baxy2.IGridItem;
//import nl.wotuu.database.DatabaseOpenHelper;
//import nl.wotuu.database.DatabaseRow;
//import nl.wotuu.database.annotations.DatabaseExclude;
//import proguard.annotation.KeepPublicClassMemberNames;
//
///**
// * Created by FrankkieNL on 16-7-13.
// */
//@KeepPublicClassMemberNames
//public class DatabaseAppInfo extends DatabaseRow implements IGridItem{
//
//    public String title = "";
//    public String packageName = "";
//    public String componentName;
//    public int isFavorite = 0;
//    public String folder = "";
//    public String lastOpened = ""; //DatabaseRow does not support long, so String it is.
//    public int timesOpened = 0;
//    public int isOUYA = 0; //DatabaseRow does not support booleans?! int 0 & 1 it is..
//    public int isOUYAGame = 0;
//
//    @DatabaseExclude
//    public Drawable icon;
//
//    @DatabaseExclude
//    public String animationPath;
//
//    @DatabaseExclude
//    public Intent intent;
//
//
//    public DatabaseAppInfo() {
//        super(DatabaseOpenHelper.GetInstance().GetTableName(DatabaseAppInfo.class));
//    }
//
//    public DatabaseAppInfo(int id) {
//        super(DatabaseOpenHelper.GetInstance().GetTableName(DatabaseAppInfo.class), id);
//    }
//
//    public void setFavorite(boolean favo){
//        if (favo){
//            isFavorite = 1;
//        } else {
//            isFavorite = 0;
//        }
//    }
//
//    public void setOUYA(boolean ouya){
//        if (ouya){
//            isOUYA = 1;
//        } else {
//            isOUYA = 0;
//        }
//    }
//
//    public void setOUYAGame(boolean ouyaGame){
//        if (ouyaGame){
//            isOUYAGame = 1;
//        } else {
//            isOUYAGame = 0;
//        }
//    }
//
//    @Override
//    public void setFolderName(String folder){
//        this.folder = folder;
//    }
//
//    /**
//     * Creates the application intent based on a component name and various launch flags.
//     *
//     * @param className   the class name of the component representing the intent
//     * @param launchFlags the launch flags
//     */
//    public void setActivity(ComponentName className, int launchFlags) {
//        intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setComponent(className);
//        intent.setFlags(launchFlags);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof DatabaseAppInfo)) {
//            return false;
//        }
//
//        DatabaseAppInfo that = (DatabaseAppInfo) o;
//        return title.equals(that.title) &&
//                intent.getComponent().getClassName().equals(
//                        that.intent.getComponent().getClassName());
//    }
//
//    @Override
//    public String getTitle() {
//        return title;
//    }
//
//    @JSON(include = false)
//    @Override
//    public Drawable getImage() {
//        return icon;
//    }
//
//    @JSON(include = false)
//    @Override
//    public boolean isOUYA() {
//        return (isOUYA == 1);
//    }
//
//    @JSON(include = false)
//    @Override
//    public boolean isOUYAGame() {
//        return (isOUYAGame == 1);
//    }
//
//    @JSON(include = false)
//    @Override
//    public boolean isFavorite() {
//        return (isFavorite == 1);
//    }
//
//    @JSON(include = false)
//    @Override
//    public boolean isFolder() {
//        return false;
//    }
//
//    @JSON(include = false)
//    @Override
//    public boolean isInFolder() {
//        return (folder != null && folder.length() > 1);
//    }
//
//    @JSON(include = false)
//    @Override
//    public String getFolderName() {
//        return folder;
//    }
//
//    @JSON(include = false)
//    @Override
//    public int getGridWidth() {
//        return 1;
//    }
//
//    @JSON(include = false)
//    @Override
//    public int getGridHeight() {
//        return 1;
//    }
//
//    @JSON(include = false)
//    @Override
//    public int getGridPosX() {
//        return 0;
//    }
//
//    @JSON(include = false)
//    @Override
//    public int getGridPosY() {
//        return 0;
//    }
//
//    @JSON(include = false)
//    @Override
//    public int compareTo(IGridItem iGridItem){
//        if (iGridItem.isFolder()){
//            return 1;
//        } else {
//            return getTitle().toLowerCase().compareTo(iGridItem.getTitle().toLowerCase()); //compare by name
//        }
//    }
//}
