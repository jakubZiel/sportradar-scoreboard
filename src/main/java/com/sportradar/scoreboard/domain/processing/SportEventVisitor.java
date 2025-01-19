package com.sportradar.scoreboard.domain.processing;

import com.sportradar.scoreboard.domain.event.CardEvent;
import com.sportradar.scoreboard.domain.event.GameIsLiveEvent;
import com.sportradar.scoreboard.domain.event.GameOvertimeEvent;
import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.event.SubstitutionEvent;


public interface SportEventVisitor
{
    void visit(GoalEvent goalEvent);

    void visit(SubstitutionEvent substitutionEvent);

    void visit(CardEvent substitutionEvent);

    void visit(GameOvertimeEvent gameOvertimeEvent);

    void visit(GameIsLiveEvent gameIsLiveEvent);
}
