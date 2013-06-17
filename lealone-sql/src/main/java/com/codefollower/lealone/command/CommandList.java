/*
 * Copyright 2004-2013 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package com.codefollower.lealone.command;

import java.util.ArrayList;

import com.codefollower.lealone.expression.ParameterInterface;
import com.codefollower.lealone.result.ResultInterface;

/**
 * Represents a list of SQL statements.
 */
class CommandList extends Command {

    private final Command command;
    private final String remaining;

    CommandList(Parser parser, String sql, Command c, String remaining) {
        super(parser, sql);
        this.command = c;
        this.remaining = remaining;
    }

    public ArrayList<? extends ParameterInterface> getParameters() {
        return command.getParameters();
    }

    private int executeRemaining() {
        Command remainingCommand = session.prepareLocal(remaining);
        if (remainingCommand.isQuery()) {
            remainingCommand.query(0);
            return 0;
        } else {
            return remainingCommand.update();
        }
    }

    public int update() {
        int updateCount = command.executeUpdate();
        updateCount += executeRemaining();
        return updateCount;
    }

    public ResultInterface query(int maxrows) {
        ResultInterface result = command.query(maxrows);
        executeRemaining();
        return result;
    }

    public boolean isQuery() {
        return command.isQuery();
    }

    public boolean isTransactional() {
        return true;
    }

    public boolean isReadOnly() {
        return false;
    }

    public ResultInterface queryMeta() {
        return command.queryMeta();
    }

    public int getCommandType() {
        return command.getCommandType();
    }

    @Override
    public Prepared getPrepared() {
        return command.getPrepared();
    }

}
