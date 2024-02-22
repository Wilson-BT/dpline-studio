package com.dpline.flink.explain;

import lombok.Data;
import org.apache.flink.table.operations.CatalogSinkModifyOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;

import java.util.ArrayList;
import java.util.List;

@Data
public class SqlOperationGroup {

    /**
     * 所有操作集合
     */
    private List<ModifyOperation> modifyOperations = new ArrayList<>();

    /**
     * insert 语句 集合
     */
    private List<CatalogSinkModifyOperation> cateLogSinkOperations = new ArrayList<>();

    /**
     * 查询集合
     */
    private List<QueryOperation> queryOperation = new ArrayList<>();

    /**
     * not exec operation
     */
    private List<Operation> unExecOperations = new ArrayList<>();


    public void addModifyOperation(ModifyOperation modifyOperation){
        modifyOperations.add(modifyOperation);
    }

    public void addCatalogSinkModifyOperation(CatalogSinkModifyOperation cateLogSinkModifyOperation){
        cateLogSinkOperations.add(cateLogSinkModifyOperation);
    }

    public void addUnExecOperation(Operation operation){
        unExecOperations.add(operation);
    }

}
