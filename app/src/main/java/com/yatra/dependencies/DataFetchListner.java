package com.yatra.dependencies;


public interface DataFetchListner {
    // void onDeliverAllData(List<DataModel> dataModels);

    void onDeliverData(dependencies dataModel);
   // void onDeliverData1(Categories dataModel);

    void onHideDialog();
}
