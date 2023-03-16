package com.hufudb.openhufu.benchmark;

import static org.junit.Assert.assertEquals;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hufudb.openhufu.core.table.GlobalTableConfig;
import com.hufudb.openhufu.user.OpenHuFuUser;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;

public class OpenHuFuBenchmarkTest {
  private static final Logger LOG = LoggerFactory.getLogger(OpenHuFuBenchmark.class);
  private static final OpenHuFuUser user = new OpenHuFuUser();

  @BeforeClass
  public static void setUp() throws IOException {

    List<String> endpoints =
        new Gson().fromJson(Files.newBufferedReader(
                Path.of(OpenHuFuBenchmark.class.getClassLoader().getResource("endpoints.json")
                    .getPath())),
            new TypeToken<ArrayList<String>>() {
            }.getType());
    List<GlobalTableConfig> globalTableConfigs =
        new Gson().fromJson(Files.newBufferedReader(
                Path.of(OpenHuFuBenchmark.class.getClassLoader().getResource("tables.json")
                    .getPath())),
            new TypeToken<ArrayList<GlobalTableConfig>>() {
            }.getType());
    LOG.info("Init benchmark of OpenHuFu...");
    for (String endpoint : endpoints) {
      user.addOwner(endpoint, null);
    }

    for (GlobalTableConfig config : globalTableConfigs) {
      user.createOpenHuFuTable(config);
    }
    LOG.info("Init finish");
  }

  public void printLine(ResultSet it) throws SQLException {
    for (int i = 1; i <= it.getMetaData().getColumnCount(); i++) {
      System.out.print(it.getString(i) + "|");
    }
    System.out.println();
  }

  @Test
  public void testSelect() throws SQLException {
    String sql = "select * from nation";
    ResultSet it = user.executeQuery(sql);
    int count = 0;
    while (it.next()) {
      printLine(it);
      ++count;
    }
    assertEquals(25, count);
    it.close();
  }
  @Test
  public void testEqualJoin() throws SQLException {
    String sql = "select * from nation join region on nation.N_REGIONKEY = region.R_REGIONKEY";
    ResultSet dataSet = user.executeQuery(sql);
    int count = 0;
    while (dataSet.next()) {
      printLine(dataSet);
      ++count;
    }
    assertEquals(25, count);
    dataSet.close();
  }
  @Test
  public void testLeftJoin() throws SQLException {
    String sql = "select * from nation left join region on nation.N_REGIONKEY = region.R_REGIONKEY";
    ResultSet dataSet = user.executeQuery(sql);
    int count = 0;
    while (dataSet.next()) {
      printLine(dataSet);
      ++count;
    }
    assertEquals(25, count);
    dataSet.close();
  }
  @Test
  public void testRightJoin() throws SQLException {
    String sql = "select * from nation right join region on nation.N_REGIONKEY = region.R_REGIONKEY";
    ResultSet dataSet = user.executeQuery(sql);
    int count = 0;
    while (dataSet.next()) {
      printLine(dataSet);
      ++count;
    }
    assertEquals(25, count);
    dataSet.close();
  }
  @Test
  public void testFullJoin() throws SQLException {
    String sql = "select * from nation full join region on nation.N_REGIONKEY = region.R_REGIONKEY";
    ResultSet dataSet = user.executeQuery(sql);
    int count = 0;
    while (dataSet.next()) {
      printLine(dataSet);
      ++count;
    }
    assertEquals(25, count);
    dataSet.close();
  }
  @Test
  public void testCount() throws SQLException {
    String sql = "select count(*) from supplier";
    ResultSet dataSet = user.executeQuery(sql);
    dataSet.next();
    printLine(dataSet);
    long ans = Long.parseLong(dataSet.getString(1));
    assertEquals(30, ans);
    dataSet.close();
  }

  @Test
  public void testAvg() throws SQLException {
    String sql = "select avg(P_PARTKEY) from part";
    ResultSet dataSet = user.executeQuery(sql);
    dataSet.next();
    printLine(dataSet);
    long ans = Long.parseLong(dataSet.getString(1));
    assertEquals(100, ans);
    dataSet.close();
  }

  @Test
  public void testSum() throws SQLException {
    String sql = "select sum(P_PARTKEY) from part";
    ResultSet dataSet = user.executeQuery(sql);
    dataSet.next();
    printLine(dataSet);
    long ans = Long.parseLong(dataSet.getString(1));
    assertEquals(60300, ans);
    dataSet.close();
  }

  @Test
  public void testMax() throws SQLException {
    String sql = "select max(C_CUSTKEY) from customer";
    ResultSet dataSet = user.executeQuery(sql);
    dataSet.next();
    printLine(dataSet);
    long ans = Long.parseLong(dataSet.getString(1));
    assertEquals(150, ans);
    dataSet.close();
  }

  @Test
  public void testMin() throws SQLException {
    String sql = "select min(C_CUSTKEY) from customer";
    ResultSet dataSet = user.executeQuery(sql);
    dataSet.next();
    printLine(dataSet);
    long ans = Long.parseLong(dataSet.getString(1));
    assertEquals(1, ans);
    dataSet.close();
  }

  @Test
  public void testGroupByAndOrder() throws SQLException {
    String sql = "select count(C_CUSTKEY) from customer group by C_CUSTKEY order by C_CUSTKEY DESC";
    ResultSet dataSet = user.executeQuery(sql);
    int count = 0;
    while (dataSet.next()) {
      printLine(dataSet);
      ++count;
    }
    assertEquals(150, count);
    dataSet.close();
  }
} 
