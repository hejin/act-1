/*************************************************************************
*                                                                        *
*  This file is part of the 20n/act project.                             *
*  20n/act enables DNA prediction for synthetic biology/bioengineering.  *
*  Copyright (C) 2017 20n Labs, Inc.                                     *
*                                                                        *
*  Please direct all queries to act@20n.com.                             *
*                                                                        *
*  This program is free software: you can redistribute it and/or modify  *
*  it under the terms of the GNU General Public License as published by  *
*  the Free Software Foundation, either version 3 of the License, or     *
*  (at your option) any later version.                                   *
*                                                                        *
*  This program is distributed in the hope that it will be useful,       *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
*  GNU General Public License for more details.                          *
*                                                                        *
*  You should have received a copy of the GNU General Public License     *
*  along with this program.  If not, see <http://www.gnu.org/licenses/>. *
*                                                                        *
*************************************************************************/

package act.shared;

import java.util.HashSet;

// superceded by MongoDB.getManualMarkedReachables
@Deprecated
public class FattyAcidEnablers {

  @Deprecated
   public static HashSet<Long> getFattyAcidEnablers(int enables_at_least) {
     System.out.println("Abort: Fatty acid enablers list needs to be updated coz the chemical IDs might have changed.");
     System.exit(-1);

    HashSet<Long> ids = new HashSet<Long>();
    for (int[] entry : fatty_acid) {
      long id = (long) entry[0];
      int cluster_enabled_size = entry[1];
      if (cluster_enabled_size >= enables_at_least)
        ids.add(id);
    }
    return ids;
  }

  @Deprecated
  static int[][] fatty_acid = new int[][] {
    /* { chem_id, cluster_size_that_is enabled } */
    { 43, 476 },
    { 1026, 569 },
    { 1050, 570 },
    { 1684, 471 },
    { 2003, 478 },
    { 3064, 479 },
    { 3976, 538 },
    { 4600, 471 },
    { 5181, 505 },
    { 5758, 449 },
    { 6062, 495 },
    { 6179, 480 },
    { 6252, 501 },
    { 6254, 491 },
    { 6520, 470 },
    { 6656, 528 },
    { 6690, 536 },
    { 6699, 547 },
    { 6872, 536 },
    { 6879, 459 },
    { 7006, 501 },
    { 7012, 518 },
    { 7031, 484 },
    { 7103, 460 },
    { 7377, 569 },
    { 7796, 528 },
    { 7802, 536 },
    { 7807, 453 },
    { 8183, 487 },
    { 8200, 482 },
    { 8215, 536 },
    { 8261, 505 },
    { 8270, 492 },
    { 8271, 504 },
    { 8273, 451 },
    { 8276, 454 },
    { 8295, 453 },
    { 8320, 547 },
    { 8321, 545 },
    { 8328, 513 },
    { 8329, 493 },
    { 8375, 512 },
    { 8955, 485 },
    { 8994, 456 },
    { 9031, 511 },
    { 9035, 516 },
    { 9766, 455 },
    { 14596, 476 },
    { 16184, 558 },
    { 16257, 569 },
    { 17294, 569 },
    { 19448, 558 },
    { 20036, 495 },
    { 20101, 537 },
    { 20102, 573 },
    { 20103, 573 },
    { 20123, 574 },
    { 20124, 574 },
    { 20516, 494 },
    { 20802, 543 },
    { 20813, 540 },
    { 20814, 542 },
    { 20815, 537 },
    { 21489, 489 },
    { 21491, 537 },
    { 21514, 537 },
    { 21706, 558 },
    { 22083, 463 },
    { 22140, 539 },
    { 22141, 541 },
    { 22226, 510 },
    { 22377, 461 },
    { 23980, 486 },
    { 24460, 467 },
    { 24562, 547 },
    { 26031, 490 },
    { 27491, 510 },
    { 28108, 458 },
    { 28125, 463 },
  };

}

/*
574  [20124]
574  [20123]
573  [20103]
573  [20102]
570  [1050]
569  [7377, 17294]
569  [16257, 1026]
569  [1050, 17294]
558  [19448, 21706]
558  [16257, 16184]
547  [8320, 6699]
547  [24562, 6699]
545  [8321, 6699]
543  [20802]
542  [20814]
541  [22141]
540  [20813]
539  [22140]
538  [3976]

537  [21514]
537  [21491]
537  [20815]
537  [20101]

536  [8215, 17294]
536  [7802, 17294]
536  [6872, 17294]
536  [6690, 17294]

528  [6656, 7796]
518  [7012, 7796]
516  [9035, 6699]
513  [8328, 6699]
512  [8375]
511  [9031, 6699]
510  [27491, 22226]
505  [8261, 5181]
504  [5181, 8271]
501  [6252, 7006]
495  [20036, 6062]
494  [20516]
493  [8329]
492  [8270]
491  [7802, 6254]
490  [7377, 26031]
489  [7802, 21489]
487  [8183]
486  [23980, 6252]
485  [8955]
484  [7031]
483  [8321]
482  [8200, 6252]
480  [6179]
479  [3064, 27491]
478  [2003, 27491]
476  [14596, 43]
471  [1684, 4600]
470  [5181, 6520]
467  [27491, 24460]
465  [7802, 43]
464  [7802, 5181]
463  [28125, 22083]
462  [22083]
461  [22377]
460  [7103, 4600]
459  [6879, 7796]
458  [28108]
457  [8200, 28108]
456  [8994]
455  [9766]
454  [8276]
453  [8295, 7807]
452  [9035]
451  [8273]
450  [9031]
449  [8215, 5758]
 */


/*
 *
 * Inspired by the conditional reachability analysis that gives the following
ClusterSize Enabling chemical
574  [20124]
574  [20123]
573  [20103]
573  [20102]
570  [1050]
569  [7377, 17294]
569  [16257, 1026]
569  [1050, 17294]
558  [19448, 21706]
558  [16257, 16184]
547  [8320, 6699]
547  [24562, 6699]
545  [8321, 6699]
545  [8271, 6699]
545  [6699, 8265]
543  [20802]
542  [20814]
541  [22141]
541  [16580]
540  [21513, 4474]
540  [21513, 18042]
540  [20813]
540  [20803]
540  [17294, 16580]
539  [22140]
539  [22131]
539  [21513]
539  [21512]
539  [21511]
539  [21493]
539  [20461]
539  [19447]
539  [18617]
538  [4571, 16257]
538  [3976]
538  [22624, 14554]
538  [22610, 14554]
538  [22142, 14554]
538  [20480]
538  [19415]
538  [18562]
538  [18084]
538  [17294, 22132]
538  [17294, 22130]
538  [17294, 18061]
538  [17061]
537  [8857]
537  [26590, 17061]
537  [23949]
537  [23079, 17294]
537  [22624]
537  [22613]
537  [22610]
537  [22603, 23949]
537  [22142]
537  [22133]
537  [21514]
537  [21491]
537  [20815]
537  [20101]
537  [19448]
537  [19414]
537  [19401]
537  [18561]
537  [17767]
537  [17766]
537  [17765]
537  [17719]
537  [17681]
537  [17680]
537  [17642]
537  [17329]
537  [17294]
537  [17294, 3976]
537  [17294, 16986]
537  [17017]
537  [16257]
537  [10166]
536  [8215, 17294]
536  [7802, 17294]
536  [6872, 17294]
536  [6690, 17294]
536  [6690, 16257]
536  [4600, 16257]
536  [4573, 16257]
536  [22624, 17294]
536  [22142, 17294]
536  [19448, 17294]
536  [18618, 17294]
536  [17719, 26590]
536  [17294, 8857]
536  [17294, 7006]
536  [17294, 22133]
536  [17294, 21514]
536  [17294, 20815]
536  [17294, 20125]
536  [17294, 20036]
536  [17294, 17767]
536  [17294, 17719]
536  [17294, 17329]
536  [17294, 17017]
536  [16257, 7006]
536  [16257, 4535]
536  [16257, 20036]
536  [16257, 12301]
536  [16257, 10084]
536  [10166, 17294]
528  [6656, 7796]
518  [7012, 7796]
518  [7012, 26031]
518  [6694, 7796]
518  [15556, 7012]
516  [9035, 6699]
513  [8328, 6699]
513  [6663, 7796]
512  [8375]
512  [8374]
512  [8370]
512  [22054]
511  [9031, 6699]
511  [7802, 6699]
511  [7796, 6735]
511  [6699, 7796]
511  [4573, 6699]
511  [27702, 6699]
511  [26031, 6699]
511  [20036, 6699]
511  [19354, 6699]
511  [15556, 6699]
510  [27491, 22226]
505  [8261, 5181]
504  [5181, 8271]
504  [5181, 8186]
504  [5181, 6252]
501  [6252, 7006]
495  [20036, 6062]
494  [20516]
493  [8329]
493  [7802, 20516]
492  [8270]
492  [8269]
492  [6518]
492  [6254]
491  [7802, 6254]
491  [6518, 20036]
491  [6254, 7796]
491  [6254, 7793]
490  [7377, 26031]
490  [25105, 1062]
490  [21489]
490  [1062, 7796]
489  [7802, 21489]
489  [7793, 21489]
487  [8183]
486  [23980, 6252]
485  [8955]
485  [8324]
485  [8187]
485  [6519]
484  [7802, 6519]
484  [7031]
484  [7030]
484  [23928]
484  [21806]
484  [21131]
483  [8321]
483  [8271]
483  [8265]
483  [8186]
483  [7735, 3028]
483  [6513]
483  [6253]
483  [6252]
483  [28033]
483  [27575]
483  [27534]
483  [24123]
482  [8200, 6252]
482  [7802, 6513]
482  [7802, 6252]
482  [7802, 11260]
482  [7646, 7796]
482  [7646, 3028]
482  [6530, 6252]
482  [6252, 7796]
482  [6252, 7793]
482  [20036, 7646]
482  [20036, 6253]
482  [11260, 7796]
482  [11260, 20036]
480  [6179]
480  [20048]
480  [20002]
480  [15214]
479  [3064, 27491]
478  [2003, 27491]
476  [14596, 43]
471  [1684, 4600]
470  [5181, 6520]
470  [4931, 20036]
470  [26630, 20036]
467  [27491, 24460]
467  [26590, 7006]
465  [7802, 43]
465  [7761]
465  [7757]
465  [43, 7796]
465  [18855]
464  [7802, 5181]
464  [5181, 7796]
464  [5181, 20036]
464  [4573, 5181]
464  [15556, 5181]
463  [28125, 22083]
463  [26112, 22083]
462  [22083]
461  [27144, 22083]
461  [23980, 28108]
461  [22377]
460  [7103, 4600]
460  [6704, 7796]
460  [4573, 7103]
460  [27625, 20036]
460  [20036, 28126]
459  [6879, 7796]
459  [6879, 26031]
459  [27624, 28108]
459  [21152]
459  [21130]
459  [14627]
458  [28108]
458  [16681]
458  [15557]
458  [15555, 28108]
457  [8200, 28108]
457  [7802, 28108]
457  [7802, 27141]
457  [7793, 28108]
457  [6815]
457  [28108, 7796]
457  [28107]
457  [27141, 7796]
457  [27141, 20036]
457  [20036, 3567]
457  [20036, 28108]
457  [15918]
457  [15895]
456  [8994]
456  [8400]
456  [7802, 28107]
456  [7802, 14651]
456  [7790, 28107]
456  [6843, 7796]
456  [6530, 23980]
456  [6527]
456  [5776, 3028]
456  [5398]
456  [28107, 7796]
456  [28107, 7793]
456  [26994]
456  [20483]
456  [20036, 28107]
456  [20036, 14651]
456  [14651, 7796]
455  [9766]
455  [8399]
455  [8274]
455  [8267]
455  [7802, 23266]
455  [7796, 5957]
455  [6908]
455  [6901]
455  [6816]
455  [6752]
455  [6520]
455  [28130]
455  [26994, 28199]
455  [26994, 26031]
455  [24659]
455  [24634]
455  [24433]
455  [20163]
455  [20036, 5957]
454  [8276]
454  [7802, 5419]
454  [7697]
454  [6914, 6256]
454  [6753, 6256]
454  [6530]
454  [6528]
454  [6256]
454  [6256, 7033]
454  [5419, 7796]
454  [5419, 20036]
454  [4573, 15195]
454  [27491, 15334]
454  [20036, 6750]
454  [15062]
454  [15022]
454  [14916]
454  [14913]
453  [8295, 7807]
453  [8295, 26031]
453  [8295, 26030]
453  [7819]
453  [7802, 26591]
453  [7382, 20036]
453  [6528, 7796]
453  [6256, 8200]
453  [6256, 6530]
453  [27162, 7796]
453  [26591, 7796]
453  [26075]
453  [23897]
453  [2157]
453  [21516]
453  [21494]
453  [20036, 6528]
453  [20036, 26591]
453  [20036, 17330]
453  [14650, 7796]
452  [9035]
452  [8301]
452  [8263]
452  [8209, 26590]
452  [8182]
452  [7802, 6529]
452  [7802, 6506]
452  [7802, 25130]
452  [7674]
452  [6858, 7796]
452  [5737, 20036]
452  [5729, 7796]
452  [5421, 7796]
452  [5406, 20036]
452  [4628, 20036]
452  [3028, 5397]
452  [27428]
452  [27268]
452  [26924]
452  [24882]
452  [24848]
452  [24742]
452  [24650]
452  [24648]
452  [2419, 7796]
452  [23221]
452  [21791]
452  [21029]
452  [20036, 5730]
452  [19752]
452  [18657]
452  [17823]
452  [17822]
452  [17768]
452  [17295]
452  [16236, 26031]
452  [15921]
452  [15854]
452  [15556, 7248]
452  [14598]
452  [14596]
452  [14553]
451  [8273]
451  [7811]
451  [7808, 7796]
451  [7802, 6363]
451  [7796, 6855]
451  [7719]
451  [7704]
451  [7693]
451  [7586]
451  [7585]
451  [7502, 4600]
451  [7314]
451  [6894]
451  [6880, 7796]
451  [6695, 7796]
451  [6363, 7796]
451  [6057]
451  [6055]
451  [6022, 4600]
451  [5774]
451  [5420]
451  [5417]
451  [5395]
451  [4944]
451  [4620]
451  [4608]
451  [4584]
451  [4574]
451  [3961, 7796]
451  [28123]
451  [27491, 2089]
451  [27113]
451  [27112]
451  [26537]
451  [26053]
451  [26031, 4187]
451  [25538]
451  [25346]
451  [25064]
451  [25039]
451  [24649]
451  [24647]
451  [24419]
451  [24208, 26590]
451  [23744]
451  [23742]
451  [23605]
451  [23602]
451  [23574]
451  [23376]
451  [22615]
451  [22614]
451  [22376]
451  [20842, 7796]
451  [20818]
451  [20487]
451  [20486]
451  [20468]
451  [20140]
451  [20112]
451  [20036, 7799]
451  [20036, 11081]
451  [17812]
451  [17035, 7796]
451  [16664]
451  [15896]
451  [15854, 27144]
451  [15805]
451  [15556, 9140]
451  [15556, 8494]
451  [15556, 7999]
451  [15556, 5210]
451  [15556, 4187]
451  [15556, 10335]
451  [15554]
451  [15524, 26031]
451  [15027]
451  [14941]
451  [14653]
451  [14602]
451  [14600, 7796]
451  [14599]
450  [9031]
450  [8439]
450  [8260]
450  [8226]
450  [8220]
450  [7810]
450  [7807]
450  [7805]
450  [7802]
450  [7796]
450  [7791, 20036]
450  [7705]
450  [7579]
450  [6508, 3028]
450  [5955]
450  [5768]
450  [5758]
450  [5757]
450  [5748]
450  [4925]
450  [4629]
450  [4601]
450  [4600]
450  [4598]
450  [4573]
450  [4559]
450  [28109]
450  [28106]
450  [28032]
450  [27702]
450  [27526]
450  [27491]
450  [27451]
450  [27144]
450  [27087]
450  [27053]
450  [27052]
450  [26993]
450  [26629]
450  [26627]
450  [26590]
450  [26590, 21810]
450  [26051]
450  [26031]
450  [26031, 8417]
450  [26031, 16196]
450  [26030]
450  [25917]
450  [25149]
450  [25133]
450  [25105]
450  [21149, 26031]
450  [21140]
450  [20848]
450  [20832, 26031]
450  [20721]
450  [20485]
450  [20036]
450  [20036, 25134]
450  [19975]
450  [19452]
450  [19451]
450  [19354]
450  [19354, 25134]
450  [19076]
450  [18655, 3028]
450  [18632, 3028]
450  [18169]
450  [18154]
450  [16081]
450  [15871]
450  [15556]
450  [15063]
450  [15026]
450  [15021]
450  [14991]
450  [14987]
450  [14939]
450  [14655]
450  [14514]
449  [8215, 5758]
449  [8215, 26590]
449  [8200, 26590]
449  [7807, 7796]
449  [7807, 7793]
449  [7807, 20036]
449  [7805, 20036]
449  [7802, 7807]
449  [7802, 5758]
449  [7802, 26590]
449  [7802, 26031]
449  [7739, 20036]
449  [6071, 7807]
449  [6053, 20036]
449  [5782, 20036]
449  [5769, 7796]
449  [5758, 7796]
449  [5758, 7793]
449  [5758, 20036]
449  [5757, 7796]
449  [4629, 8215]
449  [4629, 7796]
449  [4599, 7796]
449  [4559, 5769]
449  [27052, 4535]
449  [26993, 9348]
449  [26993, 26031]
449  [26590, 7796]
449  [26590, 7793]
449  [26031, 8200]
449  [26031, 7796]
449  [26031, 7793]
449  [26031, 3028]
449  [26031, 28106]
449  [26030, 7796]
449  [25917, 7796]
449  [20848, 26993]
449  [20036, 5769]
449  [20036, 26590]
449  [20036, 26031]
449  [20036, 26030]
449  [18154, 3028]
449  [16081, 9348]
449  [15556, 4535]
449  [15556, 28106]
449  [15556, 27144]
449  [15556, 18154]
449  [15556, 15065]
449  [15556, 11301]
449  [15556, 10308]
449  [14939, 4535]
449  [14939, 14655]
449  [14514, 4535]
182  [6699, 9039]
179  [8365]
178  [7793, 6699]
148  [11260, 7793]
145  [8362]
145  [27625, 7793]
143  [8322]
141  [6512]
136  [7756]
132  [8410]
130  [7793, 26591]
130  [4911]
129  [9039]
129  [7812]
129  [27141, 7793]
128  [2419, 7793]
128  [14651, 7793]
127  [8982]
127  [8981]
127  [5776, 7793]
127  [4509]
127  [14650, 7793]
126  [7689]
126  [5736]
126  [4893]
126  [4536]
125  [7793]
125  [4535]
113  [7727]
102  [23350]

*/