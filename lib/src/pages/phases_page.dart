// Copyright (C) 2020 covid19cuba
// Use of this source code is governed by a GNU GPL 3 license that can be
// found in the LICENSE file.

import 'dart:developer';

import "package:collection/collection.dart";
import 'package:covid19cuba/src/blocs/blocs.dart';
import 'package:covid19cuba/src/models/phases/phases.dart';
import 'package:covid19cuba/src/models/phases/phases_item.dart';
import 'package:covid19cuba/src/models/phases/phases_measure.dart';
import 'package:covid19cuba/src/models/phases/phases_state.dart';
import 'package:covid19cuba/src/pages/pages.dart';
import 'package:covid19cuba/src/utils/utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:getflutter/getflutter.dart';
import 'package:separated_column/separated_column.dart';
import 'package:url_launcher/url_launcher.dart';

class PhasesPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => GeneralBloc.getPhasesBloc(),
      child: PhasesPageWidget(),
    );
  }
}

class PhasesPageWidget extends PageWidget<Phases, PhasesState> {
  PhasesPageWidget() : super(getWidgetLoaded, title: 'Fases de Desescalada');

  static Widget getWidgetLoaded(
    BuildContext context,
    LoadedGeneralState<Phases> state,
  ) {
    return PhasesWidget(phases: state.data);
  }
}

class PhasesWidget extends StatefulWidget {
  final Phases phases;

  const PhasesWidget({Key key, this.phases}) : super(key: key);

  @override
  PhasesWidgetState createState() => PhasesWidgetState(phases);
}

class PhasesWidgetState extends State<PhasesWidget> {
  final Phases phases;
  var index = 0;

  PhasesWidgetState(this.phases);

  PhasesItem get phase => phases.phases[index];

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: <Widget>[
        Container(
          margin: EdgeInsets.fromLTRB(10, 10, 10, 2.5),
          child: Card(
            color: Colors.grey[50],
            child: Container(
              margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
              child: Center(
                child: Container(
                  margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                  child: Text(
                    'Fase en la que se encuentran las provincias',
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      color: Constants.primaryColor,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
        Column(
          children:
              phases.phases.where((x) => x.provinces.length > 0).map((item) {
            return Container(
              margin: EdgeInsets.symmetric(horizontal: 10, vertical: 2.5),
              width: MediaQuery.of(context).size.width,
              child: Card(
                color: Colors.grey[50],
                child: Container(
                  margin: EdgeInsets.all(5),
                  child: Wrap(
                      alignment: WrapAlignment.start,
                      crossAxisAlignment: WrapCrossAlignment.center,
                      children: [
                            Card(
                              elevation: 0,
                              child: Container(
                                margin: EdgeInsets.all(7),
                                child: Text(
                                  item.name,
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 16,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                              color: getColor(item),
                            )
                          ] +
                          item.provinces.map((e) {
                            return Card(
                              elevation: 0,
                              child: Container(
                                margin: EdgeInsets.all(5),
                                child: Text(
                                  e,
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                  ),
                                ),
                              ),
                              color: Colors.grey,
                            );
                          }).toList()),
                ),
              ),
            );
          }).toList(),
        ),
        Container(
          margin: EdgeInsets.fromLTRB(10, 20, 10, 2.5),
          child: Card(
            color: Colors.grey[50],
            child: Column(children: <Widget>[
              Center(
                child: Container(
                  margin: EdgeInsets.all(5),
                  child: Text(
                    'Seleccione la fase',
                    style: TextStyle(
                      color: Constants.primaryColor,
                      fontSize: 16,
                    ),
                  ),
                ),
              ),
              Slider.adaptive(
                value: index + 1.0,
                onChanged: (value) {
                  setState(() {
                    index = value.toInt() - 1;
                  });
                },
                activeColor: Constants.primaryColor,
                min: 1,
                max: phases.phases.length.toDouble(),
                divisions: phases.phases.length - 1,
                label: phase.name,
              )
            ]),
          ),
        ),
        Container(
          margin: EdgeInsets.symmetric(horizontal: 10, vertical: 2.5),
          child: Card(
            color: Colors.grey[50],
            child: Container(
              margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
              child: Center(
                child: Container(
                  margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                  child: Text(
                    'Medidas de la ${phase.name} separadas por categoría',
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      color: Constants.primaryColor,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
        Column(
          children: groupBy(
              phase.measures.map((String id) => phases.measures[id]),
              (x) => x.category).entries.map((item) {
            return Container(
              margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
              child: Card(
                child: GFAccordion(
                  collapsedTitlebackgroundColor: Colors.white,
                  expandedTitlebackgroundColor: Colors.white,
                  contentbackgroundColor: Colors.white,
                  textStyle: TextStyle(
                    color: Constants.primaryColor,
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                  collapsedIcon: Icon(
                    Icons.keyboard_arrow_down,
                    color: Constants.primaryColor,
                  ),
                  expandedIcon: Icon(
                    Icons.keyboard_arrow_up,
                    color: Constants.primaryColor,
                  ),
                  title: phases.categories[item.key].name,
                  contentChild: SeparatedColumn(
                    children: item.value.map((value) {
                      return Tooltip(
                        child: Container(
                          margin: EdgeInsets.symmetric(vertical: 10),
                          child: Text(value.text),
                        ),
                        message: 'Fases: ' +
                            value.phases.toString().substring(
                                1, value.phases.toString().length - 1),
                      );
                    }).toList(),
                    separatorBuilder: (_, __) {
                      return Divider(
                        color: Constants.primaryColor,
                      );
                    },
                  ),
                ),
              ),
            );
          }).toList(),
        ),
        GestureDetector(
          child: Container(
            margin: EdgeInsets.all(20),
            child: Center(
              child: Text('Fuente: Cubadebate'),
            ),
          ),
          onTap: () async {
            final url =
                'http://www.cubadebate.cu/noticias/2020/06/22/medidas-a-implementar-en-las-tres-fases-de-la-primera-etapa-de-recuperacion-pos-covid-19-pdf';
            if (await canLaunch(url)) {
              await launch(url);
            } else {
              log('Could not launch $url');
            }
          },
        ),
      ],
    );
  }

  static Color getColor(PhasesItem item) {
    switch (item.order) {
      case 1:
        return Colors.red;
      case 2:
        return Colors.orange;
      case 3:
        return Colors.amberAccent;
      default:
        return Colors.white;
    }
  }
}
